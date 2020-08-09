package com.web.blog.Board.service;

import com.web.blog.Board.entity.*;
import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.repository.*;
import com.web.blog.Common.advice.exception.CNotOwnerException;
import com.web.blog.Common.advice.exception.CResourceNotExistException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostMemberRepository postMemberRepository;
    private final TagService tagService;
    private final ReplyService replyService;

    //게시판 단건 조회
    public Board getBoard(long boardId) {
        return boardRepository.findById(boardId).orElseThrow(CResourceNotExistException::new);
    }

    //게시판 리스트 조회
    public List<Board> getBoards(Member member) {
        List<Board> boards = boardRepository.findByMember(member);
        for (Board board : boards) {
            if (board.getName().equals("나의 Answers")) {
                boards.remove(board);
                break;
            }
        }
        return boards;
    }

    //게시판 카테고리 정보 조회
    public Board findBoard(String name, Member member) {
        return Optional.ofNullable(boardRepository.findByNameAndMember(name, member)).orElseThrow(CResourceNotExistException::new);
    }

    //게시판 카테고리 생성
    public Board createBoard(String uid, String name) {
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserNotFoundException::new);
        return boardRepository.save(Board.builder()
                .member(member)
                .name(name)
                .build());
    }

    public Board updateBoard(long boardId, String uid, String name) {
        Board board = getBoard(boardId);
        Member member = board.getMember();
        if (!uid.equals(member.getUid())) {
            throw new CNotOwnerException();
        }
        board.setUpdate(name);
        return board;
    }

    public boolean deleteBoard(long boardId, long msrl) {
        Board board = getBoard(boardId);
        Member member = board.getMember();
        List<Post> list = postRepository.findByBoard(board);
        for (Post p : list) {
            deletePost(p.getPostId());
        }
        if (msrl != member.getMsrl())
            throw new CNotOwnerException();
        boardRepository.delete(board);
        return true;
    }

    public boolean deletePost(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        tagService.deleteTags(post);
        replyService.deleteReplies(post);
        deleteLikes(post);
        postRepository.delete(post);
        return true;
    }

    public void deleteLikes(Post post) {
        List<PostMember> likers = postMemberRepository.findPostMemberByPost(post);
        for (PostMember pm : likers) {
            postMemberRepository.delete(pm);
        }
    }

    //게시판 내 포스트  list 조회
    @Transactional
    public List<OnlyPostMapping> CategoryPostList(long board_id) {
        return postRepository.findAllByBoard_BoardId(board_id);
    }

    //한 게시판 내의 게시글 전체 삭제
    public boolean deletePostsInBoard(String name) {
        Board board = boardRepository.findByName(name);
        List<Post> posts = postRepository.findByBoard(board);
        for (Post p : posts) {
            postRepository.delete(p);
        }
        return true;
    }

    //게시글 전체 삭제(회원탈퇴 시)
    public boolean deletePosts(Long msrl) {
        Member member = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
        List<Board> boards = boardRepository.findByMember(member);
        for (Board b : boards) {
            List<Post> posts = postRepository.findByBoard(b);
            for (Post p : posts) {
                List<PostTag> originalPostTags = postTagRepository.findByPost(p);
                for (PostTag pt : originalPostTags) {
                    Optional<Tag> tag = tagRepository.findByTag(pt.getTag().getTag()); //tag명으로 각 태그들을 찾고
                    Tag t = tag.get(); //t에 대입
                    if (t.getTagUsageCnt() > 1)
                        tagRepository.updateTagUsageCntMinus(t.getTagId()); //해당 태그가 1번 초과 쓰였으면 사용한 내용만 -1
                    else if (t.getTagUsageCnt() == 1) { //해당 태그가 한 번밖에 쓰지 않았으면 그냥 태그 통째로 삭제
                        tagRepository.delete(t);
                    }
                    postTagRepository.deleteById(pt.getId()); //post_tag 에서 연결 해제
                }
                postRepository.delete(p);
            }
        }
        return true;
    }

    //게시판 전체 삭제(회원탈퇴 시)
    public boolean deleteBoards(Long msrl) {
        Member member = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
        List<Board> boards = boardRepository.findByMember(member);
        for (Board b : boards) {
            boardRepository.delete(b);
        }
        return true;
    }

}
