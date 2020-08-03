package com.web.blog.Board.service;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.Reply;
import com.web.blog.Board.model.ParamPost;
import com.web.blog.Board.model.ParamReply;
import com.web.blog.Board.repository.*;
import com.web.blog.Common.advice.exception.*;
import com.web.blog.Common.service.FileService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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

    //게시판 내 포스트 검색 및 list 조회
    public List<Post> CategoryPostList(long board_id) {
        return postRepository.findByBoard_BoardId(board_id);
    }

    //게시판 내 포스트 검색 및 list 조회
    public List<Post> CategoryPostSearch(int which, long board_id, String keyword) { //검색: which = 1~4
        if (which == 1) { //통합 검색
            return postRepository.searchCategoryPosts(board_id, keyword);
        } else if (which == 2) { //제목 검색
            return postRepository.findByBoard_BoardIdAndSubjectContaining(board_id, keyword);
        } else if (which == 3) { //내용 검색
            return postRepository.findByBoard_BoardIdAndContentContaining(board_id, keyword);
        } else { //태그 검색
            return postRepository.findByBoard_BoardIdAndTagContaining(board_id, keyword);
        }
    }

    //블로그 내 포스트 검색 및 list 조회
    public List<Post> BlogPostList(String writer) {
        return postRepository.findByWriter(writer);
    }

    //블로그 내 포스트 검색 및 list 조회
    public List<Post> BlogPostSearch(int which, String writer, String keyword) { //검색: which = 1~4
        if (which == 1) { //통합 검색
            return postRepository.searchBlogPosts(writer, keyword);
        } else if (which == 2) { //제목 검색
            return postRepository.findByWriterAndSubjectContaining(writer, keyword);
        } else if (which == 3) { //내용 검색
            return postRepository.findByWriterAndContentContaining(writer, keyword);
        } else { //태그 검색
            return postRepository.findByWriterAndTagContaining(writer, keyword);
        }
    }

    //사이트 내 포스트 검색 및 list 조회
    public List<Post> SitePostList() {
        return postRepository.findAll();
    }

    //사이트 내 포스트 검색 및 list 조회
    public List<Post> SitePostSearch(int which, String keyword) { //검색: which = 1~5
        if (which == 1) { //통합 검색
            return postRepository.searchEveryBlogPosts(keyword);
        } else if (which == 2) { //제목 검색
            return postRepository.findBySubjectContaining(keyword);
        } else if (which == 3) { //내용 검색
            return postRepository.findByContentContaining(keyword);
        } else if (which == 4) { //태그 검색
            return postRepository.findByTagContaining(keyword);
        } else { //작성자 검색
            return postRepository.findByWriterContaining(keyword);
        }
    }


    //게시글 단건 조회
    public Post getPost(long postId) {
        postRepository.updateViewCnt(postId);
        return postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
    }

    public Post getPost(long postId, Board board) {
        postRepository.updateViewCnt(postId);
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        if (post.getBoard().equals(board)) return post;
        else return null;
    }

    //게시글 작성
    public Post writePost(String nickname, String boardName, ParamPost paramPost, MultipartFile[] files, Member member) throws IOException { //, MultipartFile[] files
        Board board = findBoard(boardName, member);
        if (files != null) {
            fileService.uploadFiles(files);
        }
//        Post post = new Post(board, nickname, paramPost.getSubject(), paramPost.getContent(), paramPost.getTag());
        return postRepository.save(Post.builder()
                .board(board)
                .writer(nickname)
                .subject(paramPost.getSubject())
                .content(paramPost.getContent())
                .tag(paramPost.getTag())
                .build());
    }

    //게시글 좋아요
    public void likePost(Member member, Post post, Boolean like) {
        long msrl = member.getMsrl();
        long postId = post.getPost_id();
        String nickname = post.getWriter();
        Member writer = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        if (like) {
            memberRepository.updateScoreIfLiked(writer.getMsrl());
            postRepository.updateLikeCntPlus(postId);
            postMemberRepository.insertLike(msrl, postId);
        } else {
            memberRepository.updateScoreIfUnliked(writer.getMsrl());
            postRepository.updateLikeCntMinus(postId);
            postMemberRepository.deleteLike(msrl, postId);
        }
    }

    //유저가 좋아요 한 글 목록
    public List<Post> likedPostList(Member member) {
        long msrl = member.getMsrl();
        List<Long> list = postMemberRepository.likedPost(msrl);
        List<Post> posts = new ArrayList<>();
        for (Long id : list) {
            Post post = postRepository.findById(id).orElseThrow(CResourceNotExistException::new);
            posts.add(post);
        }
        return posts;
    }

    //게시글에 좋아요를 누른 유저 목록
    public List<String> likedMemberList(Post post) {
        long post_id = post.getPost_id();
        List<Long> list = postMemberRepository.likedMember(post_id);
        List<String> members = new ArrayList<>();
        for (Long id : list) {
            Member member = memberRepository.findById(id).orElseThrow(CUserNotFoundException::new);
            members.add(member.getNickname());
        }
        return members;
    }

    //게시글 수정
    public Post updatePost(String boardname, long postId, long msrl, ParamPost paramPost, MultipartFile[] files) throws IOException {
        Member member = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
        Board board = boardRepository.findByNameAndMember(boardname, member);
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        Board board1 = post.getBoard();
        if (board.equals(board1)) {
            if (files != null) {
                fileService.uploadFiles(files);
            }
            try {
                post.setUpdate(paramPost.getSubject(), paramPost.getContent(), paramPost.getTag());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return post;
    }

    //게시글 삭제
    public boolean deletePost(long postId, String uid) {
        Post post = getPost(postId);
        Board board = post.getBoard();
        Member member = board.getMember();
        if (!uid.equals(member.getUid()))
            throw new CNotOwnerException();
        postRepository.delete(post);
        return true;
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

    //댓글 상세조회
    public Reply getOneReply(long reply_id) {
        return replyRepository.findById(reply_id).orElseThrow(CResourceNotExistException::new);
    }

    //한 포스트의 댓글 리스트 조회
    public List<Reply> getRepliesofOnePost(long post_id) {
        Post post = postRepository.findById(post_id).orElseThrow(CResourceNotExistException::new);
        return replyRepository.findByPost(post);
    }

    //댓글 작성
    public Reply writeReply(Post post, Member member, ParamReply paramReply, MultipartFile[] files) throws IOException {
        if (files != null) {
            fileService.uploadFiles(files);
        }
        if (!post.getWriter().equals(member.getNickname())) {
            postRepository.updateReplyCnt(post.getPost_id());
            return replyRepository.save(Reply.builder()
                    .post(post)
                    .writer(member.getNickname())
                    .reply(paramReply.getReply())
                    .build());
        } else throw new CAskedQuestionException();
    }

    //댓글 수정
    public Reply updateReply(long reply_id, ParamReply paramReply, MultipartFile[] files) throws IOException {
        Reply reply = replyRepository.findById(reply_id).orElseThrow(CResourceNotExistException::new);
        if (files != null) {
            fileService.uploadFiles(files);
        }
        reply.setUpdate(paramReply.getReply());
        return reply;
    }

    //댓글 삭제
    public boolean deleteReply(long reply_id, Member member) {
        Reply reply = replyRepository.findById(reply_id).orElseThrow(CResourceNotExistException::new);
        if (reply.getWriter().equals(member.getNickname())) {
            replyRepository.delete(reply);
            return true;
        } else if (!reply.getWriter().equals(member.getNickname())) throw new CNotOwnerException();

        return false;
    }

    //댓글 추천
    public void likeThisReply(Member member, Reply reply, Boolean like) { //member는 로그인 한 사용자
        long msrl = member.getMsrl();
        long reply_id = reply.getReply_id();
        String writer = reply.getWriter();
        Member answerer = memberRepository.findByNickname(writer).orElseThrow(CUserNotFoundException::new); //answerer은 답변자
        if (like) {
            memberRepository.updateScoreIfLiked(answerer.getMsrl());
            replyRepository.updateLikeCntPlus(reply_id);
            replyMemberRepository.insertLike(msrl, reply_id);
        } else {
            memberRepository.updateScoreIfUnliked(answerer.getMsrl());
            replyRepository.updateLikeCntMinus(reply_id);
            replyMemberRepository.deleteLike(msrl, reply_id);
        }
    }
}
