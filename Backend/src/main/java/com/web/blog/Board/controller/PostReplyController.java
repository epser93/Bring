package com.web.blog.Board.controller;

import com.web.blog.Board.entity.*;
import com.web.blog.Board.model.OnlyReplyMapping;
import com.web.blog.Board.model.ParamReply;
import com.web.blog.Board.model.ReplyUploadsDto;
import com.web.blog.Board.repository.*;
import com.web.blog.Board.service.ReplyService;
import com.web.blog.Board.service.ReplyUploadsService;
import com.web.blog.Common.advice.exception.CAlreadyLikedException;
import com.web.blog.Common.advice.exception.COwnerCannotLike;
import com.web.blog.Common.advice.exception.CUserExistException;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Common.service.S3Service;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Api(tags = {"J. Post Reply"})
@RequiredArgsConstructor
@RestController
public class PostReplyController {
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final ReplyMemberRepository replyMemberRepository;
    private final ReplyService replyService;
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final ReplyUploadsService replyUploadsService;
    private final ReplyUploadsRepository replyUploadsRepository;

    //댓글 상세조회
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 상세조회", notes = "댓글 상세조회")
    @GetMapping(value = "/reply/{replyId}")
    public ListResult<OnlyReplyMapping> answerDetail(@PathVariable long replyId) {
        return responseService.getListResult(replyService.getOneReply(replyId));
    }

    //한 포스트의 댓글 리스트 조회
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 목록", notes = "댓글 목록")
    @GetMapping(value = "/reply/{postId}/replies")
    public ListResult<ListResult> getAllAnswersinOnePost(@PathVariable long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        List<OnlyReplyMapping> list = replyRepository.findByPost_PostId(postId);
        result.add(responseService.getListResult(list));

        int cnt = 0;
        if (logined.isPresent()) {
            for (OnlyReplyMapping rm : list) {
                amIInTheList.add(false);
                long replyId = rm.getReplyId();
                if (replyMemberRepository.findByMember_MsrlAndReply_ReplyId(logined.get().getMsrl(), replyId).isPresent()) {
                    amIInTheList.remove(cnt);
                    amIInTheList.add(true);
                }
                //파일 조회
                if (replyUploadsRepository.findByReplyId(replyId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<ReplyUploadsDto> files = replyUploadsService.getList(replyId);
                    ReplyUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                }
                cnt++;
            }
        } else {
            for (OnlyReplyMapping rm : list) {
                long replyId = rm.getReplyId();
                amIInTheList.add(false);
                //파일 조회
                if (replyUploadsRepository.findByReplyId(replyId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<ReplyUploadsDto> files = replyUploadsService.getList(replyId);
                    ReplyUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                }
            }
        }
        result.add(responseService.getListResult(amIInTheList));
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }

    //댓글 작성
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 작성", notes = "댓글 작성")
    @PostMapping(value = "/reply/{postId}")
    public SingleResult<Reply> answerTheQuestion(@PathVariable long postId, @Valid @RequestBody ParamReply paramReply) throws IOException {
        Optional<Post> post = postRepository.findById(postId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Reply reply = replyService.writeReply(post.get(), logined.get(), paramReply);
        List<ReplyUploads> list = replyUploadsRepository.findAllByNickname(logined.get().getNickname());
        String content = reply.getReply();
        int num = 0;
        for (ReplyUploads ru : list) {
            String filep = ru.getFilePath();
            if (ru.getReplyId() == -100 && !replyRepository.findByReplyIdAndReplyContaining(reply.getReplyId(), filep).isPresent()) { //db에 저장된 파일 경로가 해당 포스트의 내용에 포함되어 있지 않으면~
                s3Service.delete(filep);
                replyUploadsRepository.deleteById(ru.getId());
            } else if (ru.getReplyId() == -100 && postRepository.findByPostIdAndContentContaining(reply.getReplyId(), filep).isPresent()) {
                String original = ru.getFilePath();
                String rename = s3Service.rename(filep, ru.getFileName(), reply.getReplyId(), num, logined.get().getNickname());
                replyUploadsRepository.updateFilePath(rename, ru.getId());
                replyUploadsRepository.updateReplyId(reply.getReplyId(), ru.getId());
                content = content.replace(original, rename);
                num++;
            }
        }
        replyRepository.updateReply(content, reply.getReplyId());
        return responseService.getSingleResult(reply);
    }

    //댓글 수정
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 수정", notes = "댓글 수정")
    @PutMapping(value = "/reply/{replyId}")
    public SingleResult<Reply> updateAnswer(@Valid @RequestBody ParamReply paramReply, @PathVariable long replyId) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Reply reply = replyService.updateReply(replyId, paramReply);
        List<ReplyUploads> list = replyUploadsRepository.findAllByNickname(logined.get().getNickname());
        String content = reply.getReply();
        int num = 0;
        for (ReplyUploads ru : list) {
            String filep = ru.getFilePath();
            if (ru.getReplyId() == -100 && !replyRepository.findByReplyIdAndReplyContaining(reply.getReplyId(), filep).isPresent()) { //db에 저장된 파일 경로가 해당 포스트의 내용에 포함되어 있지 않으면~
                s3Service.delete(filep);
                replyUploadsRepository.deleteById(ru.getId());
            } else if (ru.getReplyId() == -100 && postRepository.findByPostIdAndContentContaining(reply.getReplyId(), filep).isPresent()) {
                String original = ru.getFilePath();
                String rename = s3Service.rename(filep, ru.getFileName(), reply.getReplyId(), num, logined.get().getNickname());
                replyUploadsRepository.updateFilePath(rename, ru.getId());
                replyUploadsRepository.updateReplyId(reply.getReplyId(), ru.getId());
                content = content.replace(original, rename);
                num++;
            }
        }
        replyRepository.updateReply(content, reply.getReplyId());
        return responseService.getSingleResult(reply);
    }

    //댓글 삭제
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 삭제", notes = "댓글 삭제")
    @DeleteMapping(value = "/reply/{replyId}")
    public CommonResult deleteAnswer(@PathVariable long replyId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        Optional<Reply> reply = replyRepository.findById(replyId);
        Post post = reply.get().getPost();
        Boolean isOk = replyService.deleteReply(replyId, member);
        if (isOk) {
            return responseService.getSuccessResult();
        }
        return null;
    }

    //댓글 좋아요
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 추천", notes = "댓글 추천")
    @PostMapping(value = "/reply/like/{replyId}/{replyer}")
    public SingleResult<Integer> like(@RequestBody Boolean likeit, @PathVariable long replyId, @PathVariable String replyer) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new); //로그인한 사용자
        Optional<Reply> reply = replyRepository.findById(replyId);
        Optional<ReplyMember> replyMember = replyMemberRepository.findByMemberAndReply(member, reply.get());
        int like = 0;
        if (replyMember.isPresent() && likeit) {
            throw new CAlreadyLikedException();
        } else if (replyMember.isPresent() && !likeit) { //추천을 이미 한 사용자면서 추천을 취소하면
            replyService.likeThisReply(member, reply.get(), likeit);
            List<OnlyReplyMapping> findReply = replyService.getOneReply(replyId);
            OnlyReplyMapping onlyReplyMapping = findReply.get(0);
            like = onlyReplyMapping.getLikes();
        } else {
            if (!member.getNickname().equals(replyer) && likeit) { //로그인 한 사용자가 댓글 작성자가 아니고~
                replyService.likeThisReply(member, reply.get(), likeit);
                List<OnlyReplyMapping> findReply = replyService.getOneReply(replyId);
                OnlyReplyMapping onlyReplyMapping = findReply.get(0);
                like = onlyReplyMapping.getLikes();
            } else if (member.getNickname().equals(replyer)) throw new COwnerCannotLike();
        }
        return responseService.getSingleResult(like);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "파일 등록", notes = "새로운 댓글 작성 시 파일 등록")
    @PostMapping(value = "/reply/{postId}/newuploads")
    public ListResult<String> upload(@PathVariable long postId, @RequestPart MultipartFile[] files) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member logined = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        return responseService.getListResult(replyService.saveFiles(-100, logined.getNickname(), files));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "파일 수정 등록", notes = "기존 댓글 수정 할 때, 필요시 파일 수정")
    @PostMapping(value = "/reply/{replyId}/uploads")
    public ListResult<String> uploadUpdate(@PathVariable long replyId, @RequestPart MultipartFile[] files) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member logined = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        return responseService.getListResult(replyService.saveFiles(replyId, logined.getNickname(), files));
    }
}
