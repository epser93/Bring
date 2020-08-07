package com.web.blog.QnA.controller;

import com.web.blog.Board.entity.Post;
import com.web.blog.Board.model.ParamPost;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.service.PostService;
import com.web.blog.Board.service.TagService;
import com.web.blog.Common.advice.exception.CAnsweredQuestionException;
import com.web.blog.Common.advice.exception.CResourceNotExistException;
import com.web.blog.Common.advice.exception.CSelectedAnswerException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.OnlyMemberMapping;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.QnA.entity.Apost;
import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.model.OnlyApostMapping;
import com.web.blog.QnA.model.OnlyQpostMapping;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Api(tags = {"7. Questions"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/questions")
public class QuestionController {
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final QTagService qTagService;
    private final QnaService qnaService;
    private final ApostRepository apostRepository;
    private final QpostRepository qpostRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final TagService tagService;

    @ApiOperation(value = "QnA 전체 질문 리스트(최신글)", notes = "QnA의 모든 질문글 리스트(최신글)")
    @GetMapping("/recent")
    public ListResult<OnlyQpostMapping> listAllQpostsRecent() {
        LocalDateTime date = LocalDateTime.now();
        date.minus(30, ChronoUnit.DAYS);
        return responseService.getListResult(qpostRepository.findByCreatedAtLessThanEqualOrderByCreatedAtDesc(date));
    }

    @ApiOperation(value = "QnA 전체 질문 리스트(인기글)", notes = "QnA의 모든 질문글 리스트(인기글)")
    @GetMapping("/trend")
    public ListResult<OnlyQpostMapping> listAllQpostsTrend() {
        LocalDateTime date = LocalDateTime.now();
        date.minus(30, ChronoUnit.DAYS);
        return responseService.getListResult(qpostRepository.findDistinctByViewsGreaterThanEqualAndCreatedAtLessThanEqualOrAnswerCntGreaterThanEqualAndCreatedAtLessThanEqualOrderByCreatedAtDesc(20, date, 1, date));
    }

    @ApiOperation(value = "QnA 특정 유저 질문 리스트", notes = "한 유저의 모든 질문글 리스트")
    @GetMapping("/{nickname}/qlist")
    public ListResult<OnlyQpostMapping> listUserQposts(@PathVariable String nickname) {
        return responseService.getListResult(qpostRepository.findByWriter(nickname));
    }

    //질문 조회
    @ApiOperation(value = "질문 조회", notes = "질문 조회")
    @GetMapping(value = "/{qpostId}")
    public ListResult<ListResult> questionDetail(@PathVariable long qpostId) {
        List<ListResult> results = new ArrayList<>();
        results.add(responseService.getListResult(qnaService.getOneQpost(qpostId)));
        results.add(responseService.getListResult(qTagService.getTags(qpostId)));
        results.add(responseService.getListResult(qnaService.getApostsofOneQpost(qpostId)));
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
        ParamPost paramPost = new ParamPost();

        Set<String> tags = paramQpost.getTags();
        List<SingleResult> result = new ArrayList<>();
        Qpost qpost1 = qpostRepository.findById(qpostId).get();
        if(qpost1.getSelectOver()) throw new CSelectedAnswerException();
        Qpost qpost = qnaService.updateQuestion(member, qpostId, paramQpost, files);
        if (!tags.isEmpty()) {
            for (String tag : tags) {
                qTagService.updateTag(qpost, tag);
            }
        }

        List<OnlyApostMapping> apost = apostRepository.findByQpost(qpost);
        for (OnlyApostMapping oam : apost) {
            //블로그 Q&A 게시판
            OnlyMemberMapping oamMem = memberRepository.findAllByNickname(oam.getWriter()).orElseThrow(CUserNotFoundException::new);
            StringBuilder sb = new StringBuilder();
            sb.append("[Q&A]\"" + qpost.getSubject() + "\"에 대한 나의 답변(질문번호: " + qpost.getQpostId() + ", 질문자: " + qpost.getWriter() + ")");
            paramPost.setSubject(sb.toString());
            sb = new StringBuilder();
            sb.append("Q." + qpost.getSubject() + System.getProperty("line.separator"));
            sb.append(qpost.getContent() + System.getProperty("line.separator") + System.getProperty("line.separator"));
            sb.append("A." + System.getProperty("line.separator"));
            sb.append(oam.getAnswer() + System.getProperty("line.separator"));
            paramPost.setContent(sb.toString());
            paramPost.setOriginal((long) -1);
            postService.updatePost("나의 Answers", oam.getPostId(), oamMem.getMsrl(), paramPost, null);
            Post answer = postRepository.findById(oam.getPostId()).orElseThrow(CResourceNotExistException::new);

            Set<String> tagSet = new HashSet<>(qTagService.getTags(qpost.getQpostId()));
            List<String> tags2 = new ArrayList<>(tagSet);
            if (!tags.isEmpty()) {
                for (String tag : tags2) {
                    tagService.insertTags(answer, tag);
                }
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
    public CommonResult deleteQuestion(@PathVariable long qpostId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> member = memberRepository.findByUid(uid);
        Boolean isOk = qnaService.deleteQuestion(qpostId, member.get());
        if (isOk) {
            return responseService.getSuccessResult();
        }
        return null;
    }
}
