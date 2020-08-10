package com.web.blog.Board.repository;

import com.web.blog.Board.entity.Reply;
import com.web.blog.Board.entity.ReplyMember;
import com.web.blog.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ReplyMemberRepository extends JpaRepository<ReplyMember, Long> {
    Optional<ReplyMember> findByMemberAndReply(Member member, Reply reply);
    Optional<ReplyMember> findByMember_MsrlAndReply_ReplyId(long msrl, long reply_id);
    List<ReplyMember> findByReply(Reply reply);
    @Modifying
    @Transactional
    @Query(value = "insert into reply_member (msrl, reply_id) values (:msrl, :reply_id)", nativeQuery = true)
    int insertLike(@Param("msrl") long msrl, @Param("reply_id") long reply_id);

    @Modifying
    @Transactional
    @Query(value = "delete from reply_member where msrl = :msrl and reply_id = :reply_id", nativeQuery = true)
    int deleteLike(@Param("msrl") long msrl, @Param("reply_id") long reply_id);
}
