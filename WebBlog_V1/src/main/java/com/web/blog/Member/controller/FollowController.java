package com.web.blog.Member.controller;

import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.model.PostUploadsDto;
import com.web.blog.Board.repository.PostMemberRepository;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.repository.PostUploadsRepository;
import com.web.blog.Board.service.PostUploadsService;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.model.Paging;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.ProfileImgDto;
import com.web.blog.Member.repository.FollowRepository;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.service.FollowService;
import com.web.blog.Member.service.ProfileImgService;
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

@Api(tags = {"3. Subscribe"})
@RequiredArgsConstructor
@RestController
public class FollowController {
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final FollowService followService;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PostMemberRepository postMemberRepository;
    private final PostUploadsRepository postUploadsRepository;
    private final PostUploadsService postUploadsService;
    private final ProfileImgService profileImgService;

    //팔로우 기능
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "팔로우 보내기", notes = "팔로우 보내기")
    @PostMapping(value = "/follow/{msrl}")
    public CommonResult sendFollow(@PathVariable long msrl) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> member = memberRepository.findByUid(uid);
        if (member.isPresent()) System.out.println("fasdfasdfasdgasgas");
        long from = member.get().getMsrl();
        if (from != msrl) {
            Member fromm = memberRepository.findById(from).orElseThrow(CUserNotFoundException::new);
            Member tom = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
            followService.followFunction(fromm, tom);
            return responseService.getSuccessResult();
        } else return responseService.getFailResult(-1004, "자기 자신을 팔로우할 수 없습니다.");
    }

    //팔로우 해제 기능
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "언팔로우", notes = "팔로우 해제")
    @DeleteMapping(value = "/follow/{msrl}")
    public CommonResult sendUnfollow(@PathVariable long msrl) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> member = memberRepository.findByUid(uid);
        long from = member.get().getMsrl();
        Member fromm = memberRepository.findById(from).orElseThrow(CUserNotFoundException::new);
        Member tom = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
        followService.unFollow(fromm, tom);
        return responseService.getSuccessResult();
    }

    //팔로잉 리스트
    @ApiOperation(value = "팔로잉 목록", notes = "팔로잉 목록")
    @GetMapping(value = "/follow/ings/{msrl}")
    public ListResult<List> getFollowingList(@PathVariable long msrl) {
        Member member = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
        return responseService.getListResult(followService.followingList(member));
    }

    //팔로워 리스트
    @ApiOperation(value = "팔로워 목록", notes = "팔로워 목록")
    @GetMapping(value = "/follow/ers/{msrl}")
    public ListResult<List> getFollowersList(@PathVariable long msrl) {
        Member member = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
        return responseService.getListResult(followService.followersList(member));
    }

    //팔로잉 피드
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "팔로잉 피드", notes = "사용자가 팔로우 하는 사람들의 최신글")
    @GetMapping(value = "/follow/{msrl}/feed")
    public ListResult<ListResult> getFollowingFeed(@PathVariable long msrl, @RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = memberRepository.findByUid(uid);
        LocalDateTime date = LocalDateTime.now();
        date.minus(3, ChronoUnit.DAYS);
        List<ListResult> results = new ArrayList<>();
        List<OnlyPostMapping> semiFinalList = new ArrayList<>();
        List<OnlyPostMapping> finalList = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        List<Long> followings = followRepository.followingMember(logined.get().getMsrl());
        semiFinalList = followService.feedCaching(followings, msrl, no);
        Paging paging = new Paging((long) 1);
//        for (long userNo : followings) {
//            Member following = memberRepository.findById(userNo).get();
//            List<OnlyPostMapping> list = postRepository.findAllByMember_NicknameAndBoard_NameNotLikeAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(following.getNickname(), "나의 Answers", date, PageRequest.of(paging.getPageNo() - 1, 3));
//            for (OnlyPostMapping opm : list) {
//                semiFinalList.add(opm);
//            }
//        }
//        Collections.shuffle(semiFinalList);
        for (int i = 0; i < 8 && i + (no - 1) * 8 < semiFinalList.size(); i++) {
            finalList.add(semiFinalList.get((int) (i + (no - 1) * 8)));
        }
        results.add(responseService.getListResult(finalList));

        int cnt = 0;
        for (OnlyPostMapping pm : finalList) { //전체 포스트 리스트 for문
            long postId = pm.getPostId();
            Member memberForImg = memberRepository.findByNickname(pm.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
            amIInTheList.add(false);
            if (postMemberRepository.findPostMemberByMember_MsrlAndPost_PostId(logined.get().getMsrl(), postId).isPresent()) {
                amIInTheList.remove(cnt);
                amIInTheList.add(true);
            }
            //파일 조회
            if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                List<PostUploadsDto> files = postUploadsService.getList(postId);
                PostUploadsDto file = files.get(0);
                filePaths.add(file.getImgFullPath());
            } else {
                ProfileImgDto profileImgDto = profileImgService.getOneImg(memberForImg.getMsrl());
                String filePath = profileImgDto.getImgFullPath();
                filePaths.add(filePath);
            }
            cnt++;
        }
        results.add(responseService.getListResult(amIInTheList));
        results.add(responseService.getListResult(filePaths));
        return responseService.getListResult(results);
    }
}
