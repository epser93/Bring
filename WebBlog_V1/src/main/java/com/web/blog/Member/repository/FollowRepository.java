package com.web.blog.Member.repository;

import com.web.blog.Member.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findByFromAndTo(long from_member_fk, long to_member_fk);

    @Modifying
    @Transactional
    @Query(value = "insert into follow (from_member_fk, to_member_fk) values (:from_member_fk, :to_member_fk)", nativeQuery = true)
    int insertFollowing(@Param("from_member_fk") long from_member_fk, @Param("to_member_fk") long to_member_fk);

    @Modifying
    @Transactional
    @Query(value = "delete from follow where from_member_fk = :from_member_fk and to_member_fk = :to_member_fk", nativeQuery = true)
    int unFollowing(@Param("from_member_fk") long from_member_fk, @Param("to_member_fk") long to_member_fk);

    //팔로잉 리스트
    @Transactional
    @Query(value = "select to_member_fk from follow where from_member_fk = :from_member_fk", nativeQuery = true)
    List<Long> followingMember(@Param("from_member_fk") long from_member_fk);

    //팔로워 리스트
    @Transactional
    @Query(value = "select from_member_fk from follow where to_member_fk = :to_member_fk", nativeQuery = true)
    List<Long> followerMember(@Param("to_member_fk") long to_member_fk);

    int countByFrom_MsrlAndTo_Msrl(long from, long to);
}
