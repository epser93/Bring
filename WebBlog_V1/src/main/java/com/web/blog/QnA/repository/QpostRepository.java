package com.web.blog.QnA.repository;

import com.web.blog.Member.entity.Member;
import com.web.blog.QnA.entity.Qpost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface QpostRepository extends JpaRepository<Qpost, Long> {
    List<Qpost> findByMember(Member member);

    @Modifying
    @Transactional
    @Query(value = "update qpost set select_over = true where qpost_id = :qpost_id", nativeQuery = true)
    void changeSelectedAnswerExist(@Param("qpost_id") long qpost_id);

    @Query(value = "select select_over from qpost where qpost_id = :qpost_id", nativeQuery = true)
    boolean isSelectedAnswerExist(@Param("qpost_id") long qpost_id);

    @Modifying
    @Transactional
    @Query(value = "update qpost set answer_cnt = answer_cnt + 1 where qpost_id = :qpost_id", nativeQuery = true)
    int updateAnswerCnt(@Param("qpost_id") long qpost_id);

    @Modifying
    @Transactional
    @Query(value = "update qpost set msrl = :msrl where qpost_id = :qpost_id", nativeQuery = true)
    int updateMsrl(@Param("msrl") long msrl, @Param("qpost_id") long qpost_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE qpost SET views = views + 1 where qpost_id = :qpost_id", nativeQuery = true)
    int updateViewCnt(@Param("qpost_id") long qpost_id);

    //모든 질문글 통합 검색
    @Query(value = "select distinct * from qpost where (subject like concat('%',:keyword,'%') or content like concat('%',:keyword,'%') or writer like concat('%',:keyword,'%'))", nativeQuery = true)
    List<Qpost> searchQuestions(@Param("keyword") String keyword);

    //모든 질문글 제목 검색
    List<Qpost> findBySubjectContaining(String keyword);

    //모든 질문글 내용 검색
    List<Qpost> findByContentContaining(String keyword);

    //모든 질문글 작성자 검색
    List<Qpost> findByWriterContaining(String keyword);
}
