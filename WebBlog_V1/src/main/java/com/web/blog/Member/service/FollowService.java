package com.web.blog.Member.service;

import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Common.advice.exception.CAlreadyFollowedException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.advice.exception.CYouHaveNotFollowedThisBlogerEver;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.FollowRepository;
import com.web.blog.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService implements Serializable {
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final ProfileImgService profileImgService;
    private final PostRepository postRepository;

    //팔로우 기능
    public void followFunction(Member from, Member to) {
        if (!isFollowed(from, to)) { //팔로우 한 적이 없을 시
            memberRepository.updateFollowing(from.getMsrl());
            memberRepository.updateFollower(to.getMsrl());
            followRepository.insertFollowing(from.getMsrl(), to.getMsrl());
        } else throw new CAlreadyFollowedException();
    }

    //팔로우 해제 기능
    public void unFollow(Member from, Member to) {
        if (!isFollowed(from, to)) {
            throw new CYouHaveNotFollowedThisBlogerEver();
        }
        memberRepository.updateUnfollowing(from.getMsrl());
        memberRepository.updateUnfollower(to.getMsrl());
        followRepository.unFollowing(from.getMsrl(), to.getMsrl());
    }

    //팔로잉 리스트
    public List<List> followingList(Member member) { //내가 팔로잉 하는~
        long from = member.getMsrl();
        List<Long> list = followRepository.followingMember(from);
        List<String> followings = new ArrayList<>();
        List<String> followingsImg = new ArrayList<>();
        List<List> result = new ArrayList<>();
        for (Long msrl : list) {
            Member following = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
            followings.add(following.getNickname());
            followingsImg.add(profileImgService.getOneImg(following.getMsrl()).getImgFullPath());
        }
        result.add(followings);
        result.add(followingsImg);
        return result;
    }

    //팔로워 리스트
    public List<List> followersList(Member member) { //나를 팔로우 하는~
        long to = member.getMsrl();
        List<Long> list = followRepository.followerMember(to);
        List<String> followers = new ArrayList<>();
        List<String> followersImg = new ArrayList<>();
        List<List> result = new ArrayList<>();
        for (Long msrl : list) {
            Member follower = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
            followers.add(follower.getNickname());
            followersImg.add(profileImgService.getOneImg(follower.getMsrl()).getImgFullPath());
        }
        result.add(followers);
        result.add(followersImg);
        return result;
    }

    //기팔로우 체크
    public boolean isFollowed(Member from, Member to) {
        if (followRepository.countByFrom_MsrlAndTo_Msrl(from.getMsrl(), to.getMsrl()) == 0) return false;
        return true;
    }

    public String getIpAddr(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP"); // 웹로직
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
