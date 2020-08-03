package com.web.blog.QnA.controller;

import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.model.ParamQpost;
import com.web.blog.QnA.repository.ApostMemberRepository;
import com.web.blog.QnA.repository.ApostRepository;
import com.web.blog.QnA.repository.QpostRepository;
import com.web.blog.QnA.service.QTagService;
import com.web.blog.QnA.service.QnaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Api(tags = {"7. Questions"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/questions")
public class QuestionController {
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final QTagService qTagService;
    private final QnaService qnaService;
    private final ApostMemberRepository apostMemberRepository;
    private final QpostRepository qpostRepository;
    private final ApostRepository apostRepository;

    @ApiOperation(value = "QnA 전체 질문 리스트", notes = "QnA의 모든 질문글 리스트")
    @GetMapping("/qlist")
    public ListResult<Qpost> listAllQposts() {
        return responseService.getListResult(qnaService.getAllQuestions());
    }

    @ApiOperation(value = "QnA 특정 유저 질문 리스트", notes = "한 유저의 모든 질문글 리스트")
    @GetMapping("/{nickname}/qlist")
    public ListResult<Qpost> listUserQposts(@PathVariable String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        return responseService.getListResult(qnaService.getOnesAllQuestion(member));
    }

    //질문 조회
    @ApiOperation(value = "질문 조회", notes = "질문 조회")
    @GetMapping(value = "/{qpostId}")
    public ListResult<SingleResult> questionDetail(@PathVariable long qpostId) {
        List<SingleResult> results = new ArrayList<>();
        results.add(responseService.getSingleResult(qnaService.getOneQpost(qpostId)));
        results.add(responseService.getSingleResult(qTagService.getTags(qpostId)));
        results.add(responseService.getSingleResult(qnaService.getApostsofOneQpost(qpostId)));
        return responseService.getListResult(results);
    }

    //질문 작성
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "질문 작성", notes = "질문 작성")
    @PostMapping(value = "/ask")
    public ListResult<SingleResult> writeQuestion(@Valid @RequestBody ParamQpost paramQpost, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        Set<String> tags = paramQpost.getTags();
        List<SingleResult> result = new ArrayList<>();
        Qpost qpost = qnaService.writeQuestion(member, paramQpost, files);
        if (!tags.isEmpty()) {
            for (String tag : tags) {
                qTagService.insertTags(qpost, tag);
            }
        }
        result.add(responseService.getSingleResult(qpost));
        result.add(responseService.getSingleResult(tags));
        return responseService.getListResult(result);
    }

    //질문 수정
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "질문 수정", notes = "질문 수정")
    @PutMapping(value = "/{qpostId}")
    public ListResult<SingleResult> updateQuestion(@Valid @RequestBody ParamQpost paramQpost, @PathVariable long qpostId, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        Set<String> tags = paramQpost.getTags();
        List<SingleResult> result = new ArrayList<>();
        Qpost qpost = qnaService.updateQuestion(member, qpostId, paramQpost, files);
        if (!tags.isEmpty()) {
            for (String tag : tags) {
                qTagService.updateTag(qpost, tag);
            }
        }
        result.add(responseService.getSingleResult(qpost));
        result.add(responseService.getSingleResult(tags));
        return responseService.getListResult(result);
    }

    //질문 삭제
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "질문 삭제", notes = "질문 삭제")
    @DeleteMapping(value = "/{qpostId}")
    public CommonResult deleteQuestion(@PathVariable long qpostId, @RequestParam Boolean isAnswered) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        Boolean isOk = qnaService.deleteQuestion(qpostId, member, isAnswered);
        if (isOk) {
            return responseService.getSuccessResult();
        }
        return null;
    }
}
