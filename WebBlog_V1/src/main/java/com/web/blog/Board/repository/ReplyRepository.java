package com.web.blog.Board.repository;

import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.Reply;
import com.web.blog.Board.model.OnlyReplyMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByPostOrderByReplyId(Post post);

    Optional<List<Reply>> findByWriterAndPost_PostId(String writer, long post_id);

    List<OnlyReplyMapping> findByPost(Post post);

    List<OnlyReplyMapping> findByPost_PostId(long postId);

    Optional<Reply> findById(long reply_id);

    List<OnlyReplyMapping> findByReplyId(long reply_id);

    @Modifying
    @Transactional
    @Query(value = "update reply set selected = true where reply_id = :reply_id", nativeQuery = true)
    int select(@Param("reply_id") long reply_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE reply SET likes = likes + 1 where reply_id = :reply_id", nativeQuery = true)
    int updateLikeCntPlus(@Param("reply_id") long reply_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE reply SET likes = likes - 1 where reply_id = :reply_id", nativeQuery = true)
    int updateLikeCntMinus(@Param("reply_id") long reply_id);
}
