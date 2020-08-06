package com.web.blog.Board.repository;

import com.web.blog.Board.entity.Tag;
import com.web.blog.Board.model.OnlyTagMapping;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAll(Sort sort);
    List<OnlyTagMapping> findAllByOrderByTagUsageCntAsc();

    Optional<Tag> findByTag(String tag);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tag SET tag_usage_cnt = tag_usage_cnt + 1 where tag_id = :tag_id", nativeQuery = true)
    int updateTagUsageCntPlus(@Param("tag_id") long tag_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tag SET tag_usage_cnt = tag_usage_cnt - 1 where tag_id = :tag_id", nativeQuery = true)
    int updateTagUsageCntMinus(@Param("tag_id") long tag_id);
}
