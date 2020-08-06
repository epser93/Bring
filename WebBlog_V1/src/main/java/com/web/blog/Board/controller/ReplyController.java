package com.web.blog.Board.controller;

import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.Reply;
import com.web.blog.Board.entity.ReplyMember;
import com.web.blog.Board.model.OnlyReplyMapping;
import com.web.blog.Board.model.ParamReply;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.repository.ReplyMemberRepository;
import com.web.blog.Board.repository.ReplyRepository;
import com.web.blog.Board.service.PostService;
import com.web.blog.Board.service.ReplyService;
import com.web.blog.Common.advice.exception.CAlreadyLikedException;
import com.web.blog.Common.advice.exception.COwnerCannotLike;
import com.web.blog.Common.advice.exception.CUserExistException;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.ResponseService;
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
import java.util.Optional;

@Api(tags = {"6. Blog - Post Reply"})
@RequiredArgsConstructor
@RestController
public class ReplyController {
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final ReplyMemberRepository replyMemberRepository;
    private final ReplyService replyService;
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;

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
    public ListResult<OnlyReplyMapping> getAllAnswersinOnePost(@PathVariable long postId) {
        return responseService.getListResult(replyService.getRepliesofOnePost(postId));
    }

    //댓글 작성
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 작성", notes = "댓글 작성")
    @PostMapping(value = "/reply/{postId}")
    public SingleResult<Reply> answerTheQuestion(@PathVariable long postId, @Valid @RequestBody ParamReply paramReply, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {
        Optional<Post> post = postRepository.findById(postId);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        return responseService.getSingleResult(replyService.writeReply(post.get(), member, paramReply, files));
    }

    //댓글 수정
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "댓글 수정", notes = "댓글 수정")
    @PutMapping(value = "/reply/{replyId}")
    public SingleResult<Reply> updateAnswer(@Valid @RequestBody ParamReply paramReply, @PathVariable long replyId, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {
        Optional<Reply> reply = replyRepository.findById(replyId);
        Post post = reply.get().getPost();
        return responseService.getSingleResult(replyService.updateReply(replyId, paramReply, files));
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
    public void like(@RequestBody Boolean likeit, @PathVariable long replyId, @PathVariable String replyer) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new); //로그인한 사용자
        Optional<Reply> reply = replyRepository.findById(replyId);
        Optional<ReplyMember> replyMember = replyMemberRepository.findByMemberAndReply(member, reply.get());
        if (replyMember.isPresent() && likeit) {
            throw new CAlreadyLikedException();
        } else if (replyMember.isPresent() && !likeit) { //추천을 이미 한 사용자면서 추천을 취소하면
            replyService.likeThisReply(member, reply.get(), likeit);
        } else {
            if (!member.getNickname().equals(replyer) && likeit) { //로그인 한 사용자가 댓글 작성자가 아니고~
                replyService.likeThisReply(member, reply.get(), likeit);
            } else if (member.getNickname().equals(replyer)) throw new COwnerCannotLike();
        }
    }
}
