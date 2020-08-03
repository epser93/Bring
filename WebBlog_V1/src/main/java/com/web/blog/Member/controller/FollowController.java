package com.web.blog.Member.controller;

import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.service.FollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"3. Subscribe"})
@RequiredArgsConstructor
@RestController
public class FollowController {
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final FollowService followService;

    //팔로우 기능
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "팔로우 보내기", notes = "팔로우 보내기")
    @PostMapping(value = "/follow/{msrl}")
    public CommonResult sendFollow(@PathVariable long msrl) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        long from = member.getMsrl();
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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        long from = member.getMsrl();
        Member fromm = memberRepository.findById(from).orElseThrow(CUserNotFoundException::new);
        Member tom = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
        followService.unFollow(fromm, tom);
        return responseService.getSuccessResult();
    }

    //팔로잉 리스트
    @ApiOperation(value = "팔로잉 목록", notes = "팔로잉 목록")
    @GetMapping(value = "/follow/ings/{msrl}")
    public ListResult<String> getFollowingList(@PathVariable long msrl) {
        Member member = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
        return responseService.getListResult(followService.followingList(member));
    }

    //팔로워 리스트
    @ApiOperation(value = "팔로워 목록", notes = "팔로워 목록")
    @GetMapping(value = "/follow/ers/{msrl}")
    public ListResult<String> getFollowersList(@PathVariable long msrl) {
        Member member = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
        return responseService.getListResult(followService.followersList(member));
    }
}
