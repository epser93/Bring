package com.web.blog.QnA.repository;

import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.model.OnlyQpostMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QpostRepository extends JpaRepository<Qpost, Long> {
    List<OnlyQpostMapping> findByMember_Nickname(String writer, Pageable pageable);

    List<OnlyQpostMapping> findByMember_Nickname(String writer);

    List<OnlyQpostMapping> findAllByOrderByQpostIdAsc();

    List<OnlyQpostMapping> findByQpostId(long qpost_id);

    Optional<List<Qpost>> findAllByMember_Nickname(String writer);

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
    @Query(value = "update qpost set answer_cnt = answer_cnt - 1 where qpost_id = :qpost_id", nativeQuery = true)
    int updateAnswerCntMinus(@Param("qpost_id") long qpost_id);

    @Modifying
    @Transactional
    @Query(value = "update qpost set msrl = :msrl where qpost_id = :qpost_id", nativeQuery = true)
    int updateMsrl(@Param("msrl") long msrl, @Param("qpost_id") long qpost_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE qpost SET views = views + 1 where qpost_id = :qpost_id", nativeQuery = true)
    int updateViewCnt(@Param("qpost_id") long qpost_id);

    //모든 질문글 통합 검색
//    @Query(value = "select distinct * from qpost where (subject like concat('%',:keyword,'%') or content like concat('%',:keyword,'%') or writer like concat('%',:keyword,'%'))", nativeQuery = true)
    List<OnlyQpostMapping> findDistinctBySubjectContainingOrContentContainingOrMember_NicknameContaining(String keyword1, String keyword2, String keyword3, Pageable pageable);

    //모든 질문글 제목 검색
    List<OnlyQpostMapping> findBySubjectContaining(String keyword, Pageable pageable);

    //모든 질문글 내용 검색
    List<OnlyQpostMapping> findByContentContaining(String keyword, Pageable pageable);

    //모든 질문글 작성자 검색
    List<OnlyQpostMapping> findByMember_NicknameContaining(String keyword, Pageable pageable);

    //사이트의 모든 포스트 리스트(최신글)
    List<OnlyQpostMapping> findByCreatedAtLessThanEqualOrderByCreatedAtDesc(LocalDateTime date, Pageable pageable);

    //사이트의 모든 포스트 리스트(인기글)
    List<OnlyQpostMapping> findDistinctByViewsGreaterThanEqualAndCreatedAtLessThanEqualOrAnswerCntGreaterThanEqualAndCreatedAtLessThanEqualOrderByCreatedAtDesc(int views, LocalDateTime dateV, int answerCnt, LocalDateTime dateL, Pageable pageable);


}
