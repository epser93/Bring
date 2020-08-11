package com.web.blog.Board.repository;

import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findByPost(Post post);

    List<PostTag> findByPost_PostId(long postId);

    @Modifying
    @Transactional
    @Query(value = "insert into post_tag (post_id, tag_id) values (:post_id, :tag_id)", nativeQuery = true)
    int insertTag(@Param("post_id") long post_id, @Param("tag_id") long tag_id);
}
