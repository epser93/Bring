package com.web.blog.Board.service;

import com.web.blog.Board.entity.*;
import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.model.ParamPost;
import com.web.blog.Board.repository.*;
import com.web.blog.Common.advice.exception.CNotOwnerException;
import com.web.blog.Common.advice.exception.CResourceNotExistException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.service.FileService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.OnlyMemberMapping;
import com.web.blog.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostMemberRepository postMemberRepository;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final BoardService boardService;
    private final FileService fileService;
    private final TagService tagService;
    private final ReplyService replyService;

    //게시글 단건 조회
    public List<OnlyPostMapping> getPost(long postId) {
        postRepository.updateViewCnt(postId);
        return postRepository.findByPostId(postId);
    }

    //게시글 작성
    public Post writePost(String nickname, String boardName, ParamPost paramPost, MultipartFile[] files, Member member) throws IOException { //, MultipartFile[] files
        Board board = boardService.findBoard(boardName, member);
        if (files != null) {
            fileService.uploadFiles(files);
        }
        return postRepository.save(Post.builder()
                .board(board)
                .writer(nickname)
                .subject(paramPost.getSubject())
                .content(paramPost.getContent())
                .build());
    }

    //게시글 좋아요
    public void likePost(Member member, Post post, Boolean like) {
        long msrl = member.getMsrl();
        long postId = post.getPostId();
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
    public List<OnlyMemberMapping> likedMemberListMM(long post_id) {
        List<Long> list = postMemberRepository.likedMember(post_id);
        List<OnlyMemberMapping> members = new ArrayList<>();
        for (Long id : list) {
            Optional<OnlyMemberMapping> member = memberRepository.findByMsrl(id);
            members.add(member.get());
        }
        return members;
    }

    //게시글에 좋아요를 누른 유저 목록
    public List<String> likedMemberList(long post_id) {
        List<Long> list = postMemberRepository.likedMember(post_id);
        List<String> members = new ArrayList<>();
        for (Long id : list) {
            Optional<Member> member = memberRepository.findById(id);
            members.add(member.get().getNickname());
        }
        return members;
    }

    public OnlyMemberMapping getOnlyMember(Member member) {
        Optional<OnlyMemberMapping> mm = memberRepository.findByMsrl(member.getMsrl());
        return mm.get();
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
                post.setUpdate(paramPost.getSubject(), paramPost.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return post;
    }

    //모든 좋아요 삭제
    public void deleteLikes(Post post) {
        List<PostMember> likers = postMemberRepository.findPostMemberByPost(post);
        for (PostMember pm : likers) {
            postMemberRepository.delete(pm);
        }
    }

    //게시글 삭제
    public boolean deletePost(long postId, Member member) {
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        String writer = post.getWriter();
        Optional<Member> member2 = memberRepository.findByNickname(writer);
        if (!member2.get().getNickname().equals(member.getNickname())) throw new CNotOwnerException();
        tagService.deleteTags(post);
        replyService.deleteReplies(post);
        deleteLikes(post);
        postRepository.delete(post);
        return true;
    }
}
