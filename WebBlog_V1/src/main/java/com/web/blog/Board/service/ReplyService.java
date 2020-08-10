package com.web.blog.Board.service;

import com.web.blog.Board.entity.*;
import com.web.blog.Board.model.OnlyReplyMapping;
import com.web.blog.Board.model.ParamReply;
import com.web.blog.Board.model.ReplyUploadsDto;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.repository.ReplyMemberRepository;
import com.web.blog.Board.repository.ReplyRepository;
import com.web.blog.Board.repository.ReplyUploadsRepository;
import com.web.blog.Common.advice.exception.CNotOwnerException;
import com.web.blog.Common.advice.exception.CResourceNotExistException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.service.S3Service;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ReplyMemberRepository replyMemberRepository;
    private final ReplyRepository replyRepository;
    private final S3Service s3Service;
    private final ReplyUploadsRepository replyUploadsRepository;
    private final ReplyUploadsService replyUploadsService;

    //댓글 상세조회
    public List<OnlyReplyMapping> getOneReply(long reply_id) {
        return replyRepository.findByReplyId(reply_id);
    }

    //한 포스트의 댓글 리스트 조회
    public List<OnlyReplyMapping> getRepliesofOnePost(long post_id) {
        Post post = postRepository.findById(post_id).orElseThrow(CResourceNotExistException::new);
        return replyRepository.findByPost(post);
    }

    public boolean saveFiles(long replyId, String nickname, MultipartFile[] files) throws IOException {
        if (files != null) {
            if (replyUploadsRepository.findByReplyId(replyId).isPresent()) { //댓글에 사진이 한장이라도 존재하면~
                List<ReplyUploads> beforeUpdate = replyUploadsRepository.findAllByReplyId(replyId);
                replyUploadsService.deleteImgs(replyId); //해당하는 댓글의 모든 사진 정보 db에서 삭제
                for (ReplyUploads upload : beforeUpdate) { //해당하는 댓글의 모든 사진 s3에서 삭제
                    s3Service.delete(upload.getFilePath());
                }
            }

            int num = 0;
            for (MultipartFile file : files) { //s3에 업로드하고 db에 파일 정보 저장
                String imgPath = s3Service.upload(file, replyId, num, nickname); //s3에 저장
                ReplyUploadsDto replyUploadsDto = new ReplyUploadsDto();
                replyUploadsDto.setFilePath(imgPath);
                replyUploadsDto.setReplyId(replyId);
                replyUploadsDto.setNum(num);
                replyUploadsService.savePost(replyUploadsDto);
                num++;
            }
        }
        return true;
    }

    //댓글 작성
    public Reply writeReply(Post post, Member member, ParamReply paramReply) {
        postRepository.updateReplyCnt(post.getPostId());
        return replyRepository.save(Reply.builder()
                .post(post)
                .writer(member.getNickname())
                .reply(paramReply.getReply())
                .build());
    }

    //댓글 수정
    public Reply updateReply(long reply_id, ParamReply paramReply) {
        Reply reply = replyRepository.findById(reply_id).orElseThrow(CResourceNotExistException::new);
        reply.setUpdate(paramReply.getReply());
        return reply;
    }

    //댓글 삭제
    public boolean deleteReply(long reply_id, Member member) {
        Reply reply = replyRepository.findById(reply_id).orElseThrow(CResourceNotExistException::new);
        Post post = reply.getPost();
        if (reply.getWriter().equals(member.getNickname())) {
            replyRepository.delete(reply);
            postRepository.updateReplyCntMinus(post.getPostId());
            return true;
        } else if (!reply.getWriter().equals(member.getNickname())) throw new CNotOwnerException();

        if (replyUploadsRepository.findByReplyId(reply_id).isPresent()) { //댓글에 사진이 한장이라도 존재하면~
            List<ReplyUploads> beforeDelete = replyUploadsRepository.findAllByReplyId(reply_id);
            replyUploadsService.deleteImgs(reply_id); //해당하는 댓글의 모든 사진 정보 db에서 삭제
            for (ReplyUploads upload : beforeDelete) { //해당하는 댓글의 모든 사진 s3에서 삭제
                s3Service.delete(upload.getFilePath());
            }
        }

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

    public void deleteReplies(Post post) {
        List<Reply> list = replyRepository.findByPostOrderByReplyId(post);
        for (Reply r : list) {
            List<ReplyMember> originalReplyMember = replyMemberRepository.findByReply(r);
            for (ReplyMember rm : originalReplyMember) {
                replyMemberRepository.delete(rm);
            }
            replyRepository.delete(r);
        }
    }


}
