package com.web.blog.QnA.repository;

import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.entity.QpostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface QpostTagRepository extends JpaRepository<QpostTag, Long> {
    List<QpostTag> findByQpost(Qpost qpost);

    List<QpostTag> findByQpost_QpostId(long qpost_id);

    @Modifying
    @Transactional
    @Query(value = "insert into qpost_tag (qpost_id, tag_id) values (:qpost_id, :tag_id)", nativeQuery = true)
    int insertTag(@Param("qpost_id") long qpost_id, @Param("tag_id") long tag_id);

    @Modifying
    @Transactional
    @Query(value = "delete from qpost_tag where qpost_id = :qpost_id and tag_id = :tag_id", nativeQuery = true)
    int deleteTag(@Param("qpost_id") long qpost_id, @Param("tag_id") long tag_id);
}
