package com.web.blog.QnA.controller;

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
import com.web.blog.QnA.repository.QpostRepository;
import com.web.blog.QnA.repository.QpostUploadsRepository;
import com.web.blog.QnA.service.QpostUploadsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Api(tags = {"N. QnA List"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/questions")
public class QnaListController {
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final QpostRepository qpostRepository;
    private final QpostUploadsRepository qpostUploadsRepository;
    private final QpostUploadsService qpostUploadsService;
    private final ProfileImgService profileImgService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "QnA 전체 질문 리스트(최신글)", notes = "QnA의 모든 질문글 리스트(최신글)")
    @GetMapping("/recent")
    public ListResult<ListResult> listAllQpostsRecent(@RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        date.minus(30, ChronoUnit.DAYS);
        List<OnlyQpostMapping> list = qpostRepository.findByCreatedAtLessThanEqualOrderByCreatedAtDesc(date, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
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

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "QnA 전체 질문 리스트(인기글)", notes = "QnA의 모든 질문글 리스트(인기글)")
    @GetMapping("/trend")
    public ListResult<ListResult> listAllQpostsTrend(@RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        date.minus(30, ChronoUnit.DAYS);
        List<OnlyQpostMapping> list = qpostRepository.findDistinctByViewsGreaterThanEqualAndCreatedAtLessThanEqualOrAnswerCntGreaterThanEqualAndCreatedAtLessThanEqualOrderByCreatedAtDesc(20, date, 1, date, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
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

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "QnA 특정 유저 질문 리스트", notes = "한 유저의 모든 질문글 리스트")
    @GetMapping("/{nickname}/qlist")
    public ListResult<ListResult> listUserQposts(@PathVariable String nickname, @RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        date.minus(30, ChronoUnit.DAYS);
        List<OnlyQpostMapping> list = qpostRepository.findByMember_NicknameOrderByQpostIdDesc(nickname, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
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
