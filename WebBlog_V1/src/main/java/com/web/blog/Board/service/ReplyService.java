package com.web.blog.Board.service;

import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.Reply;
import com.web.blog.Board.entity.ReplyMember;
import com.web.blog.Board.model.OnlyReplyMapping;
import com.web.blog.Board.model.ParamReply;
import com.web.blog.Board.repository.*;
import com.web.blog.Common.advice.exception.CAskedQuestionException;
import com.web.blog.Common.advice.exception.CNotOwnerException;
import com.web.blog.Common.advice.exception.CResourceNotExistException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.service.FileService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ReplyMemberRepository replyMemberRepository;
    private final ReplyRepository replyRepository;
    private final FileService fileService;

    //댓글 상세조회
    public List<OnlyReplyMapping> getOneReply(long reply_id) {
        return replyRepository.findByReplyId(reply_id);
    }

    //한 포스트의 댓글 리스트 조회
    public List<OnlyReplyMapping> getRepliesofOnePost(long post_id) {
        Post post = postRepository.findById(post_id).orElseThrow(CResourceNotExistException::new);
        return replyRepository.findByPost(post);
    }


    //댓글 작성
    public Reply writeReply(Post post, Member member, ParamReply paramReply, MultipartFile[] files) throws IOException {
        if (files != null) {
            fileService.uploadFiles(files);
        }

        postRepository.updateReplyCnt(post.getPostId());
        return replyRepository.save(Reply.builder()
                .post(post)
                .writer(member.getNickname())
                .reply(paramReply.getReply())
                .build());
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
        long reply_id = reply.getReplyId();
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

    public void deleteReplies(Post post){
        List<Reply> list = replyRepository.findByPostOrderByReplyId(post);
        for(Reply r : list) {
            List<ReplyMember> originalReplyMember = replyMemberRepository.findByReply(r);
            for (ReplyMember rm : originalReplyMember) {
                replyMemberRepository.delete(rm);
            }
            replyRepository.delete(r);
        }
    }


}
