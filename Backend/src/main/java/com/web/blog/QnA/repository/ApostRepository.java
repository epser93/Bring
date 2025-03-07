package com.web.blog.QnA.repository;

import com.web.blog.QnA.entity.Apost;
import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.model.OnlyApostMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ApostRepository extends JpaRepository<Apost, Long> {
    List<Apost> findByMember_NicknameOrderByApostIdDesc(String writer);

    List<OnlyApostMapping> findByQpostOrderByApostIdDesc(Qpost qpost);

    List<OnlyApostMapping> findByQpost(Qpost qpost);

    List<OnlyApostMapping> findByApostId(long apost_id);

    List<OnlyApostMapping> findAllByMember_Nickname(String writer);

    Optional<List<Apost>> findByMember_MsrlAndQpost_QpostIdOrderByApostIdAsc(long msrl, long qpost_id);

    @Modifying
    @Transactional
    @Query(value = "update apost set selected = true where apost_id = :apost_id", nativeQuery = true)
    int select(@Param("apost_id") long apost_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE apost SET likes = likes + 1 where apost_id = :apost_id", nativeQuery = true)
    int updateLikeCntPlus(@Param("apost_id") long apost_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE apost SET likes = likes - 1 where apost_id = :apost_id", nativeQuery = true)
    int updateLikeCntMinus(@Param("apost_id") long apost_id);
}
