package com.web.blog.QnA.controller;

import com.web.blog.Board.service.SearchService;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.model.Paging;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.ProfileImgDto;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.service.ProfileImgService;
import com.web.blog.QnA.model.OnlyQpostMapping;
import com.web.blog.QnA.model.QpostUploadsDto;
import com.web.blog.QnA.repository.QpostUploadsRepository;
import com.web.blog.QnA.service.QnaService;
import com.web.blog.QnA.service.QpostUploadsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Api(tags = {"O. QnA Search"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/questions")
public class QnaSearchController {
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final QnaService qnaService;
    private final SearchService searchService;
    private final QpostUploadsRepository qpostUploadsRepository;
    private final QpostUploadsService qpostUploadsService;
    private final ProfileImgService profileImgService;

    //사이트의 모든 질문글 검색
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "모든 질문글 검색 ", notes = "type 1: 제목 검색, type 2: 내용 검색, type 3: 작성자 검색, type 4: 통합검색, ")
    @GetMapping(value = "/search/all_questions/{keyword}/{type}")
    public ListResult<ListResult> searchAlgorithm(@PathVariable int type, @PathVariable(required = false) String keyword, @RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();

        List<OnlyQpostMapping> list = qnaService.QuestionSearch(type, keyword, paging);
        result.add(responseService.getListResult(list));
        List<String> filePaths = new ArrayList<>();
        int cnt = 0;
        if (logined.isPresent()) {
            for (OnlyQpostMapping pm : list) {
                Member member = memberRepository.findByNickname(pm.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
                long qpostId = pm.getQpostId();
                //파일 조회
                if (qpostUploadsRepository.findByQpostId(qpostId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<QpostUploadsDto> files = qpostUploadsService.getList(qpostId);
                    QpostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
                cnt++;
            }
        } else {
            for (OnlyQpostMapping pm : list) {
                long qpostId = pm.getQpostId();
                Member member = memberRepository.findByNickname(pm.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
                //파일 조회
                if (qpostUploadsRepository.findByQpostId(qpostId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<QpostUploadsDto> files = qpostUploadsService.getList(qpostId);
                    QpostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
            }
        }
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }

    @ApiOperation(value = "전체 질문글 태그 검색", notes = "전체 질문글 태그로 검색")
    @PostMapping(value = "/search/tags/{keyword}")
    public ListResult<ListResult> searchAllQuestionssByTag(@PathVariable String keyword, @RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        date.minus(30, ChronoUnit.DAYS);
        List<OnlyQpostMapping> list = searchService.AllQnaTagSearch(keyword, paging);
        result.add(responseService.getListResult(list));
        if (logined.isPresent()) {
            for (OnlyQpostMapping qm : list) {
                Member member = memberRepository.findByNickname(qm.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
                long qpostId = qm.getQpostId();
                //파일 조회
                if (qpostUploadsRepository.findByQpostId(qpostId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<QpostUploadsDto> files = qpostUploadsService.getList(qpostId);
                    QpostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
            }
        } else {
            for (OnlyQpostMapping qm : list) {
                Member member = memberRepository.findByNickname(qm.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
                long qpostId = qm.getQpostId();
                //파일 조회
                if (qpostUploadsRepository.findByQpostId(qpostId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<QpostUploadsDto> files = qpostUploadsService.getList(qpostId);
                    QpostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
            }
        }
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }

    @ApiOperation(value = "특정 유저 질문글 태그 검색", notes = "특정 유저의 질문글 태그로 검색")
    @PostMapping(value = "/{nickname}/search/tags/{keyword}")
    public ListResult<ListResult> searchOnesQuestionsByTag(@PathVariable String nickname, @PathVariable String keyword, @RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        date.minus(30, ChronoUnit.DAYS);
        List<OnlyQpostMapping> list = searchService.OnesQnaTagSearch(nickname, keyword, paging);
        result.add(responseService.getListResult(list));
        if (logined.isPresent()) {
            for (OnlyQpostMapping qm : list) {
                Member member = memberRepository.findByNickname(qm.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
                long qpostId = qm.getQpostId();
                //파일 조회
                if (qpostUploadsRepository.findByQpostId(qpostId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<QpostUploadsDto> files = qpostUploadsService.getList(qpostId);
                    QpostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
            }
        } else {
            for (OnlyQpostMapping qm : list) {
                Member member = memberRepository.findByNickname(qm.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
                long qpostId = qm.getQpostId();
                //파일 조회
                if (qpostUploadsRepository.findByQpostId(qpostId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<QpostUploadsDto> files = qpostUploadsService.getList(qpostId);
                    QpostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
            }
        }
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }
}
