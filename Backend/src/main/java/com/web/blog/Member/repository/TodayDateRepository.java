package com.web.blog.Member.repository;

import com.web.blog.Member.entity.TodayDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodayDateRepository extends JpaRepository<TodayDate, Long> {
    List<TodayDate> findByMember_Msrl(long msrl);
}
