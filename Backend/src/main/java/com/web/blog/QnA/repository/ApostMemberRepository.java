package com.web.blog.QnA.repository;

import com.web.blog.Member.entity.Member;
import com.web.blog.QnA.entity.Apost;
import com.web.blog.QnA.entity.ApostMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ApostMemberRepository extends JpaRepository<ApostMember, Long> {
    Optional<ApostMember> findByMemberAndApost(Member member, Apost apost);

    Optional<ApostMember> findApostMemberByMember_MsrlAndApost_ApostId(long msrl, long apot_id);

    @Modifying
    @Transactional
    @Query(value = "insert into apost_member (msrl, apost_id) values (:msrl, :apost_id)", nativeQuery = true)
    int insertLike(@Param("msrl") long msrl, @Param("apost_id") long apost_id);

    @Modifying
    @Transactional
    @Query(value = "delete from apost_member where msrl = :msrl and apost_id = :apost_id", nativeQuery = true)
    int deleteLike(@Param("msrl") long msrl, @Param("apost_id") long apost_id);

    //답변을 추천한 유저 목록
    @Transactional
    @Query(value = "select msrl from apost_member where apost_id = :apost_id", nativeQuery = true)
    List<Long> likedMember(@Param("apost_id") long apost_id);
}
