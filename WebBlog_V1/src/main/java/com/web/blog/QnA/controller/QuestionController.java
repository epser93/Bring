package com.web.blog.QnA.controller;

import com.web.blog.Board.entity.Post;
import com.web.blog.Board.model.ParamPost;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.service.PostService;
import com.web.blog.Board.service.TagService;
import com.web.blog.Common.advice.exception.CResourceNotExistException;
import com.web.blog.Common.advice.exception.CSelectedAnswerException;
import com.web.blog.Common.advice.exception.CUserExistException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Common.service.S3Service;
import com.web.blog.Member.entity.IpAddrForTodayCnt;
import com.web.blog.Member.entity.IpAddrForViewCnt;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.OnlyMemberMapping;
import com.web.blog.Member.repository.IpAddrForTodayCntRepository;
import com.web.blog.Member.repository.IpAddrForViewCntRepository;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.service.FollowService;
import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.entity.QpostUploads;
import com.web.blog.QnA.model.OnlyApostMapping;
import com.web.blog.QnA.model.ParamQpost;
import com.web.blog.QnA.repository.ApostRepository;
import com.web.blog.QnA.repository.QpostRepository;
import com.web.blog.QnA.repository.QpostUploadsRepository;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Api(tags = {"L. Questions"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/questions")
public class QuestionController {
    private final FollowService followService;
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final QTagService qTagService;
    private final QnaService qnaService;
    private final ApostRepository apostRepository;
    private final QpostRepository qpostRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final TagService tagService;
    private final S3Service s3Service;
    private final QpostUploadsRepository qpostUploadsRepository;
    private final IpAddrForTodayCntRepository ipAddrForTodayCntRepository;
    private final IpAddrForViewCntRepository ipAddrForViewCntRepository;

    //질문 조회
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "질문 조회", notes = "질문 조회")
    @GetMapping(value = "/{qpostId}")
    public ListResult<ListResult> questionDetail(@PathVariable long qpostId, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Qpost> qpost = qpostRepository.findById(qpostId);
        Optional<Member> writer = memberRepository.findByNickname(qpost.get().getMember().getNickname());
        String ip = followService.getIpAddr(request);
        Optional<IpAddrForViewCnt> ipAddrQ = ipAddrForViewCntRepository.findByIpAndQpostId(ip, qpostId);
        if (!ipAddrQ.isPresent()) {
            IpAddrForViewCnt checkCnt = IpAddrForViewCnt.builder()
                    .ip(ip)
                    .postId((long) -1)
                    .qpostId(qpostId)
                    .build();
            checkCnt.setTimeout(10800L);
            ipAddrForViewCntRepository.save(checkCnt);
            qpostRepository.updateViewCnt(qpostId);
        }
        Optional<IpAddrForTodayCnt> ipAddr = ipAddrForTodayCntRepository.findByIpAndNickname(ip, writer.get().getNickname());
        if (!ipAddr.isPresent()) {
            IpAddrForTodayCnt checkCnt = IpAddrForTodayCnt.builder()
                    .ip(ip)
                    .nickname(writer.get().getNickname())
                    .build();
            checkCnt.setTimeout(86400L);
            ipAddrForTodayCntRepository.save(checkCnt);
            memberRepository.updateTodayCnt(writer.get().getMsrl());
            memberRepository.updateTotalCnt(writer.get().getMsrl());
        }

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
    public ListResult<SingleResult> writeQuestion(@Valid @RequestBody ParamQpost paramQpost) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> member = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Set<String> tags = paramQpost.getTags();
        List<SingleResult> result = new ArrayList<>();
        Qpost qpost = qnaService.writeQuestion(member.get(), paramQpost);
        List<QpostUploads> list = qpostUploadsRepository.findAllByNickname(member.get().getNickname());
        String content = qpost.getContent();
        int num = 0;
        for(QpostUploads qu : list) {
            String filep = qu.getFilePath();
            if(qu.getQpostId() == -100 && !qpostRepository.findByQpostIdAndContentContaining(qpost.getQpostId(), filep).isPresent()) { //db에 저장된 파일 경로가 해당 포스트의 내용에 포함되어 있지 않으면~
                s3Service.delete(filep);
                qpostUploadsRepository.deleteById(qu.getId());
            } else if(qu.getQpostId() == -100 && qpostRepository.findByQpostIdAndContentContaining(qpost.getQpostId(), filep).isPresent()) {
                String original = qu.getFilePath();
                String rename = s3Service.rename(filep, qu.getFileName(), qpost.getQpostId(), num, member.get().getNickname());
                qpostUploadsRepository.updateFilePath(rename, qu.getId());
                qpostUploadsRepository.updateQpostId(qpost.getQpostId(), qu.getId());
                content = content.replace(original, rename);
                num++;
            }
        }
        qpostRepository.updateContent(content, qpost.getQpostId());
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
    public ListResult<SingleResult> updateQuestion(@Valid @RequestBody ParamQpost paramQpost, @PathVariable long qpostId) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> member = Optional.ofNullable(memberRepository.findAllByUid(uid));
        ParamPost paramPost = new ParamPost();
        Set<String> tags = paramQpost.getTags();
        List<SingleResult> result = new ArrayList<>();
        Qpost qpost1 = qpostRepository.findById(qpostId).get();
        if (qpost1.getSelectOver()) throw new CSelectedAnswerException();
        Qpost qpost = qnaService.updateQuestion(member.get(), qpostId, paramQpost);
        List<QpostUploads> list = qpostUploadsRepository.findAllByNickname(member.get().getNickname());
        String content = qpost.getContent();
        int num = 0;
        for(QpostUploads qu : list) {
            String filep = qu.getFilePath();
            if(qu.getQpostId() == -100 && !qpostRepository.findByQpostIdAndContentContaining(qpost.getQpostId(), filep).isPresent()) { //db에 저장된 파일 경로가 해당 포스트의 내용에 포함되어 있지 않으면~
                s3Service.delete(filep);
                qpostUploadsRepository.deleteById(qu.getId());
            } else if(qu.getQpostId() == -100 && qpostRepository.findByQpostIdAndContentContaining(qpost.getQpostId(), filep).isPresent()) {
                String original = qu.getFilePath();
                String rename = s3Service.rename(filep, qu.getFileName(), qpost.getQpostId(), num, member.get().getNickname());
                qpostUploadsRepository.updateFilePath(rename, qu.getId());
                qpostUploadsRepository.updateQpostId(qpost.getQpostId(), qu.getId());
                content = content.replace(original, rename);
                num++;
            }
        }
        qpostRepository.updateContent(content, qpost.getQpostId());
        if (!tags.isEmpty()) {
            qTagService.deleteQtags(qpost);
            for (String tag : tags) {
                qTagService.insertTags(qpost, tag);
            }
        }

        List<OnlyApostMapping> apost = apostRepository.findByQpost(qpost);
        for (OnlyApostMapping oam : apost) {
            //블로그 Q&A 게시판
            OnlyMemberMapping oamMem = memberRepository.findAllByNickname(oam.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
            StringBuilder sb = new StringBuilder();
            sb.append("Q. " + qpost.getSubject() + "(Q writer: " + qpost.getMember().getNickname() + ", Q number: " + qpostId + ")" + System.getProperty("line.separator"));
            sb.append("\t" + qpost.getContent() + System.getProperty("line.separator"));
            paramPost.setSubject(sb.toString());
            paramPost.setContent(oam.getAnswer());
            paramPost.setOriginal((long) -1);
            postService.updatePost("나의 Answers", oam.getPostId(), oamMem.getMsrl(), paramPost);
            Post answer = postRepository.findById(oam.getPostId()).orElseThrow(CResourceNotExistException::new);

            Set<String> tagSet = new HashSet<>(qTagService.getTags(qpost.getQpostId()));
            List<String> tags2 = new ArrayList<>(tagSet);
            if (!tags.isEmpty()) {
                for (String tag : tags2) {
                    tagService.updateTag(answer, tag);
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

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "파일 등록", notes = "새로운 질문 작성 시 파일 등록")
    @PostMapping(value = "/ask/uploads")
    public ListResult<String> upload(@RequestPart MultipartFile[] files) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member logined = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        return responseService.getListResult(qnaService.saveFiles(-100, logined.getNickname(), files));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "파일 수정 등록", notes = "기존 질문 수정 할 때, 필요시 파일 수정")
    @PostMapping(value = "/ask/{qpostId}/uploads")
    public ListResult<String> uploadUpdate(@PathVariable long qpostId, @RequestPart MultipartFile[] files) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member logined = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        return responseService.getListResult(qnaService.saveFiles(qpostId, logined.getNickname(), files));
    }
}
