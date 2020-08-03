package com.web.blog.QnA.controller;

import com.web.blog.Common.advice.exception.*;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.FileService;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.QnA.entity.Apost;
import com.web.blog.QnA.entity.ApostMember;
import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.model.ParamApost;
import com.web.blog.QnA.model.ParamQpost;
import com.web.blog.QnA.repository.ApostMemberRepository;
import com.web.blog.QnA.repository.ApostRepository;
import com.web.blog.QnA.repository.QpostRepository;
import com.web.blog.QnA.service.QnaService;
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

@Api(tags = {"7. QnA"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/qna")
public class QnaController {
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final QnaService qnaService;
    private final ApostMemberRepository apostMemberRepository;
    private final QpostRepository qpostRepository;
    private final ApostRepository apostRepository;

    @ApiOperation(value = "QnA 전체 질문 리스트", notes = "QnA의 모든 질문글 리스트")
    @GetMapping("/question/qlist")
    public ListResult<Qpost> listAllQposts() {
        return responseService.getListResult(qnaService.getAllQuestions());
    }

    @ApiOperation(value = "QnA 특정 유저 질문 리스트", notes = "한 유저의 모든 질문글 리스트")
    @GetMapping("/question/{nickname}/qlist")
    public ListResult<Qpost> listUserQposts(@PathVariable String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        return responseService.getListResult(qnaService.getOnesAllQuestion(member));
    }

    @ApiOperation(value = "QnA 특정 유저 답변 리스트", notes = "한 유저의 모든 답변글 리스트")
    @GetMapping("/question/{nickname}/alist")
    public ListResult<Apost> listUserAposts(@PathVariable String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        return responseService.getListResult(qnaService.getOnesAllAnswer(member));
    }

    //질문 조회
    @ApiOperation(value = "질문 조회", notes = "질문 조회")
    @GetMapping(value = "/question/{qpostId}")
    public SingleResult<Qpost> questionDetail(@PathVariable long qpostId) {
        Qpost qpost = qnaService.getOneQpost(qpostId);
        return responseService.getSingleResult(qpost);
    }

    //질문 작성
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "질문 작성", notes = "질문 작성")
    @PostMapping(value = "/question")
    public SingleResult<Qpost> writeQuestion(@Valid @RequestBody ParamQpost paramQpost, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException { //, @RequestParam("files") MultipartFile[] files
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        return responseService.getSingleResult(qnaService.writeQuestion(member, paramQpost, files));
    }

    //질문 수정
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "질문 수정", notes = "질문 수정")
    @PutMapping(value = "/question/{qpostId}")
    public SingleResult<Qpost> updateQuestion(@Valid @RequestBody ParamQpost paramQpost, @PathVariable long qpostId, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        return responseService.getSingleResult(qnaService.updateQuestion(member, qpostId, paramQpost, files));
    }

    //질문 삭제
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "질문 삭제", notes = "질문 삭제")
    @DeleteMapping(value = "/question/{qpostId}")
    public CommonResult deleteQuestion(@PathVariable long qpostId, @RequestParam Boolean isAnswered) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        Boolean isOk = qnaService.deleteQuestion(qpostId, member, isAnswered);
        if (isOk) {
            return responseService.getSuccessResult();
        }
        return null;
    }

    //답변 상세조회
    @ApiOperation(value = "답변 상세조회", notes = "답변 상세조회")
    @GetMapping(value = "/answer/{apostId}")
    public SingleResult<Apost> answerDetail(@PathVariable long apostId) {
        return responseService.getSingleResult(qnaService.getOneApost(apostId));
    }

    //한 포스트의 답변 리스트 조회
    @ApiOperation(value = "답변 목록", notes = "답변 목록")
    @GetMapping(value = "/answer/{qpostId}/answers")
    public ListResult<Apost> getAllAnswersinOneQpost(@PathVariable long qpostId) {
        return responseService.getListResult(qnaService.getApostsofOneQpost(qpostId));
    }

    //답변 작성
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "답변 작성", notes = "답변 작성")
    @PostMapping(value = "/answer/{qpostId}")
    public SingleResult<Apost> answerTheQuestion(@PathVariable long qpostId, @Valid @RequestBody ParamApost paramApost, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {
        Qpost qpost = qnaService.getOneQpost(qpostId);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        if (qpostRepository.isSelectedAnswerExist(qpostId)) return null;
        return responseService.getSingleResult(qnaService.writeAnswer(qpost, member, paramApost, files));
    }

    //답변 수정
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "답변 수정", notes = "답변 수정")
    @PutMapping(value = "/answer/{apostId}")
    public SingleResult<Apost> updateAnswer(@Valid @RequestBody ParamApost paramApost, @PathVariable long apostId, @RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam Boolean isSelected) throws IOException {
        Apost apost = qnaService.getOneApost(apostId);
        Qpost qpost = apost.getQpost();
        return responseService.getSingleResult(qnaService.updateAnswer(apostId, paramApost, files, qpostRepository.isSelectedAnswerExist(qpost.getQpost_id())));
    }

    //답변 삭제
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "답변 삭제", notes = "답변 삭제")
    @DeleteMapping(value = "/answer/{apostId}")
    public CommonResult deleteAnswer(@PathVariable long apostId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        Apost apost = qnaService.getOneApost(apostId);
        Qpost qpost = apost.getQpost();
        Boolean isOk = qnaService.deleteAnswer(apostId, member, qpostRepository.isSelectedAnswerExist(qpost.getQpost_id()));
        if (isOk) {
            return responseService.getSuccessResult();
        }
        return null;
    }

    //답변 채택
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "답변 채택", notes = "답변 채택")
    @PostMapping(value = "/answer/select/{apostId}")
    public void select(@PathVariable long apostId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new); //로그인한 사용자
        Apost apost = qnaService.getOneApost(apostId);
        Qpost qpost = apost.getQpost();
        long msrl = qpost.getMember().getMsrl();
        if (msrl == member.getMsrl() && !qpostRepository.isSelectedAnswerExist(qpost.getQpost_id())) { //로그인 한 사용자가 질문자면~
            qnaService.selectThisAnswer(apostId, qpost.getQpost_id());
        } else throw new CNotOwnerException();
    }

    //답변 좋아요
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "답변 추천", notes = "답변 추천")
    @PostMapping(value = "/answer/like/{apostId}/{answerer}")
    public void like(@RequestBody Boolean likeit, @PathVariable long apostId, @PathVariable String answerer) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new); //로그인한 사용자
        Apost apost = qnaService.getOneApost(apostId);
        Optional<ApostMember> apostMember = apostMemberRepository.findByMemberAndApost(member, apost);
        if (apostMember.isPresent() && likeit) {
            throw new CAlreadyLikedException();
        } else if (apostMember.isPresent() && !likeit) { //추천을 이미 한 사용자면서 추천을 취소하면
            qnaService.likeThisAnswer(member, apost, likeit);
        } else {
            if (!member.getNickname().equals(answerer) && likeit) { //로그인 한 사용자가 답변 작성자가 아니고~
                qnaService.likeThisAnswer(member, apost, likeit);
            } else if (member.getNickname().equals(answerer)) throw new COwnerCannotLike();
        }
    }

    @ApiOperation(value = "답변 추천 유저 목록", notes = "해당 답변을 추천한 유저들의 목록을 보여준다.")
    @GetMapping(value = "/answer/like/{apostId}/likedusers")
    public ListResult<String> listLiked(@PathVariable long apostId) {
        Apost apost = apostRepository.findById(apostId).orElseThrow(CResourceNotExistException::new);
        return responseService.getListResult(qnaService.likedMemberList(apost));
    }
}
