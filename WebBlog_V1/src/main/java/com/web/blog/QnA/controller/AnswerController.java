package com.web.blog.QnA.controller;


import com.web.blog.Board.entity.Post;
import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.model.ParamPost;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.service.PostService;
import com.web.blog.Board.service.TagService;
import com.web.blog.Common.advice.exception.*;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.QnA.entity.Apost;
import com.web.blog.QnA.entity.ApostMember;
import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.model.OnlyApostMapping;
import com.web.blog.QnA.model.ParamApost;
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
import java.util.*;

@Api(tags = {"8. Answers"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/answers")
public class AnswerController {
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final PostService postService;
    private final QnaService qnaService;
    private final QTagService qTagService;
    private final TagService tagService;
    private final ApostMemberRepository apostMemberRepository;
    private final QpostRepository qpostRepository;
    private final ApostRepository apostRepository;
    private final PostRepository postRepository;

    @ApiOperation(value = "QnA 특정 유저 답변 리스트", notes = "한 유저의 모든 답변글 리스트")
    @GetMapping("/{nickname}/alist")
    public ListResult<Apost> listUserAposts(@PathVariable String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        return responseService.getListResult(qnaService.getOnesAllAnswer(member));
    }

    //답변 상세조회
    @ApiOperation(value = "답변 상세조회", notes = "답변 상세조회")
    @GetMapping(value = "/{apostId}")
    public ListResult<OnlyApostMapping> answerDetail(@PathVariable long apostId) {
        return responseService.getListResult(qnaService.getOneApost(apostId));
    }

    //한 포스트의 답변 리스트 조회
    @ApiOperation(value = "답변 목록", notes = "답변 목록")
    @GetMapping(value = "/{qpostId}/answers")
    public ListResult<OnlyApostMapping> getAllAnswersinOneQpost(@PathVariable long qpostId) {
        return responseService.getListResult(qnaService.getApostsofOneQpost(qpostId));
    }

    //답변 작성
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "답변 작성", notes = "답변 작성")
    @PostMapping(value = "/{qpostId}")
    public SingleResult<Apost> answerTheQuestion(@PathVariable long qpostId, @Valid @RequestBody ParamApost paramApost, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {
        Optional<Qpost> qpost = qpostRepository.findById(qpostId);
        Member asker = qpost.get().getMember();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member logined = (Member) principal;
        ParamPost paramPost = new ParamPost();

        if (asker.getMsrl() == logined.getMsrl()) {
            throw new CAskedQuestionException();
        }

        //블로그 Q&A 게시판
        StringBuilder sb = new StringBuilder();
        sb.append("[Q&A]\"" + qpost.get().getSubject() + "\"에 대한 나의 답변(질문번호: " + qpostId + ", 질문자: " + qpost.get().getWriter() + ")");
        paramPost.setSubject(sb.toString());
        sb = new StringBuilder();
        sb.append("Q." + qpost.get().getSubject() + System.getProperty("line.separator"));
        sb.append(qpost.get().getContent() + System.getProperty("line.separator") + System.getProperty("line.separator"));
        sb.append("A." + System.getProperty("line.separator"));
        sb.append(paramApost.getAnswer() + System.getProperty("line.separator"));
        paramPost.setContent(sb.toString());
        paramPost.setOriginal((long) -1);
        Post answer = new Post();
        answer = postService.writePost(logined.getNickname(), "나의 Answers", paramPost, null, logined, "");

        Set<String> tagSet = new HashSet<>(qTagService.getTags(qpostId));
        List<String> tags = new ArrayList<>(tagSet);
        if (!tags.isEmpty()) {
            for (String tag : tags) {
                tagService.insertTags(answer, tag);
            }
        }
        Apost apost = qnaService.writeAnswer(qpost.get(), logined, paramApost, files, answer.getPostId());
        if (qpostRepository.isSelectedAnswerExist(qpostId)) return null;
        return responseService.getSingleResult(apost);
    }

    //답변 수정
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "답변 수정", notes = "답변 수정")
    @PutMapping(value = "/{apostId}")
    public SingleResult<Apost> updateAnswer(@Valid @RequestBody ParamApost paramApost, @PathVariable long apostId, @RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam Boolean isSelected) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member logined = (Member) principal;
        Optional<Apost> apost = apostRepository.findById(apostId);
        long qpostId = apost.get().getQpost().getQpostId();
        Qpost qpost = qpostRepository.findById(qpostId).orElseThrow(CResourceNotExistException::new);
        ParamPost paramPost = new ParamPost();

        //블로그 Q&A 게시판
        StringBuilder sb = new StringBuilder();
        sb.append("[Q&A]\"" + qpost.getSubject() + "\"에 대한 나의 답변(질문번호: " + qpost.getQpostId() + ", 질문자: " + qpost.getWriter() + ")");
        paramPost.setSubject(sb.toString());
        sb = new StringBuilder();
        sb.append("Q." + qpost.getSubject() + System.getProperty("line.separator"));
        sb.append(qpost.getContent() + System.getProperty("line.separator") + System.getProperty("line.separator"));
        sb.append("A." + System.getProperty("line.separator"));
        sb.append(paramApost.getAnswer() + System.getProperty("line.separator"));
        paramPost.setContent(sb.toString());
        paramPost.setOriginal((long) -1);
        postService.updatePost("나의 Answers", apost.get().getPostId(), logined.getMsrl(), paramPost, null);
        Post answer = postRepository.findById(apost.get().getPostId()).orElseThrow(CResourceNotExistException::new);

        Set<String> tagSet = new HashSet<>(qTagService.getTags(qpost.getQpostId()));
        List<String> tags = new ArrayList<>(tagSet);
        if (!tags.isEmpty()) {
            for (String tag : tags) {
                tagService.insertTags(answer, tag);
            }
        }
        return responseService.getSingleResult(qnaService.updateAnswer(apostId, paramApost, files, qpostRepository.isSelectedAnswerExist(qpost.getQpostId())));
    }

    //답변 삭제
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "답변 삭제", notes = "답변 삭제")
    @DeleteMapping(value = "/{apostId}")
    public CommonResult deleteAnswer(@PathVariable long apostId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        Optional<Apost> apost = apostRepository.findById(apostId);
        Qpost qpost = apost.get().getQpost();
        Boolean isOk = qnaService.deleteAnswer(apostId, member, qpostRepository.isSelectedAnswerExist(qpost.getQpostId()));
        qpostRepository.updateAnswerCntMinus(qpost.getQpostId());
        postService.deletePost(apost.get().getPostId(), member);
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
    @PostMapping(value = "/select/{apostId}")
    public void select(@PathVariable long apostId) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new); //로그인한 사용자
        Optional<Apost> apost = apostRepository.findById(apostId);

        Qpost qpost = apost.get().getQpost();
        Optional<OnlyPostMapping> post = postRepository.findAllByPostId(apost.get().getPostId());
        long msrl = qpost.getMember().getMsrl();
        Member answerer = memberRepository.findByNickname(apost.get().getWriter()).orElseThrow(CUserNotFoundException::new);
        if (msrl == member.getMsrl() && !qpostRepository.isSelectedAnswerExist(qpost.getQpostId())) { //로그인 한 사용자가 질문자면~
            qnaService.selectThisAnswer(apostId, qpost.getQpostId(), member);
            ParamPost paramPost = new ParamPost();
            StringBuilder sb = new StringBuilder();
            sb.append("[채택]" + post.get().getSubject());
            paramPost.setSubject(sb.toString());
            paramPost.setContent(post.get().getContent());
            postService.updatePost("나의 Answers", post.get().getPostId(), answerer.getMsrl(), paramPost, null);
            System.out.println("성공");
        } else throw new CNotOwnerException();
    }

    //답변 좋아요
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "답변 추천", notes = "답변 추천")
    @PostMapping(value = "/like/{apostId}/{answerer}")
    public void like(@RequestBody Boolean likeit, @PathVariable long apostId, @PathVariable String answerer) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new); //로그인한 사용자
        Optional<Apost> apost = apostRepository.findById(apostId);
        Optional<ApostMember> apostMember = apostMemberRepository.findByMemberAndApost(member, apost.get());
        if (apostMember.isPresent() && likeit) {
            throw new CAlreadyLikedException();
        } else if (apostMember.isPresent() && !likeit) { //추천을 이미 한 사용자면서 추천을 취소하면
            qnaService.likeThisAnswer(member, apost.get(), likeit);
        } else {
            if (!member.getNickname().equals(answerer) && likeit) { //로그인 한 사용자가 답변 작성자가 아니고~
                qnaService.likeThisAnswer(member, apost.get(), likeit);
            } else if (member.getNickname().equals(answerer)) throw new COwnerCannotLike();
        }
    }

    @ApiOperation(value = "답변 추천 유저 목록", notes = "해당 답변을 추천한 유저들의 목록을 보여준다.")
    @GetMapping(value = "/like/{apostId}/likedusers")
    public ListResult<String> listLiked(@PathVariable long apostId) {
        Apost apost = apostRepository.findById(apostId).orElseThrow(CResourceNotExistException::new);
        return responseService.getListResult(qnaService.likedMemberList(apost));
    }
}
