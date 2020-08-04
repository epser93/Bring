package com.web.blog.Board.service;

import com.web.blog.Board.entity.*;
import com.web.blog.Board.model.ParamPost;
import com.web.blog.Board.model.ParamReply;
import com.web.blog.Board.model.ParamTag;
import com.web.blog.Board.repository.*;
import com.web.blog.Common.advice.exception.*;
import com.web.blog.Common.service.FileService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final PostMemberRepository postMemberRepository;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ReplyMemberRepository replyMemberRepository;
    private final ReplyRepository replyRepository;
    private final FileService fileService;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    //게시판 단건 조회
    public Board getBoard(long boardId) {
        return boardRepository.findById(boardId).orElseThrow(CResourceNotExistException::new);
    }

    //게시판 리스트 조회
    public List<Board> getBoards(Member member) {
        return boardRepository.findByMember(member);
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
        if (msrl != member.getMsrl())
            throw new CNotOwnerException();
        boardRepository.delete(board);
        return true;
    }

    //게시판 내 포스트  list 조회
    @Transactional
    public List<Post> CategoryPostList(long board_id) {
        Board board = boardRepository.findById(board_id).orElseThrow(CResourceNotExistException::new);
        return postRepository.findByBoard(board);
    }

    //블로그 내 포스트 list 조회
    public List<Post> BlogPostList(String writer) {
        return postRepository.findByWriter(writer);
    }

    //사이트 내 포스트 list 조회
    public List<Post> SitePostList() {
        return postRepository.findAll();
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
                        tagRepository.updateTagUsageCntMinus(t.getTag_id()); //해당 태그가 1번 초과 쓰였으면 사용한 내용만 -1
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
