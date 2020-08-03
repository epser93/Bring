package com.web.blog.Member.service;

import com.web.blog.Common.advice.exception.CAlreadyFollowedException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.advice.exception.CYouHaveNotFollowedThisBlogerEver;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.FollowRepository;
import com.web.blog.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

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
    public List<String> followingList(Member member) { //내가 팔로잉 하는~
        long from = member.getMsrl();
        List<Long> list = followRepository.followingMember(from);
        List<String> followings = new ArrayList<>();
        for (Long msrl : list) {
            Member following = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
            followings.add(following.getNickname());
        }
        return followings;
    }

    //팔로워 리스트
    public List<String> followersList(Member member) { //나를 팔로우 하는~
        long to = member.getMsrl();
        List<Long> list = followRepository.followerMember(to);
        List<String> followers = new ArrayList<>();
        for (Long msrl : list) {
            Member follower = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
            followers.add(follower.getNickname());
        }
        return followers;
    }

    //기팔로우 체크
    public boolean isFollowed(Member from, Member to) {
        if (followRepository.countByFrom_MsrlAndTo_Msrl(from.getMsrl(), to.getMsrl()) == 0) return false;
        return true;
    }
}
