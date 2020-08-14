package com.web.blog.Member.util;

import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TodayCntScheduler {
    private final MemberRepository memberRepository;

    public TodayCntScheduler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void initializeTodayCnt() {
        List<Member> list = memberRepository.findAll();
        for(Member member : list) {
            memberRepository.initializeTodayCnt(member.getMsrl());
        }
    }
}
