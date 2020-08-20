package com.web.blog.Member.util;

import com.web.blog.Member.entity.Member;
import com.web.blog.Member.entity.TodayDate;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.repository.TodayDateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class TodayCntScheduler {
    private final MemberRepository memberRepository;
    private final TodayDateRepository todayDateRepository;

    public TodayCntScheduler(MemberRepository memberRepository, TodayDateRepository todayDateRepository) {
        this.memberRepository = memberRepository;
        this.todayDateRepository = todayDateRepository;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void initializeTodayCnt() {
        List<Member> list = memberRepository.findAll();
        LocalDate date = LocalDate.now();
        date.minus(1, ChronoUnit.DAYS);
        for (Member member : list) {
            todayDateRepository.save(TodayDate.builder()
                    .date(date)
                    .cnt(member.getTodayCnt())
                    .member(member)
                    .build());
            memberRepository.initializeTodayCnt(member.getMsrl());
        }
    }
}
