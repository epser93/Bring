package com.web.blog.Board.repository;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.entity.Post;
import com.web.blog.Board.model.OnlyPostMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBoard(Board board);
    List<OnlyPostMapping> findAllByBoard_BoardId(long board_id);

    @Query(value = "select * from post where post_id = :post_id", nativeQuery = true)
    List<OnlyPostMapping> postById(@Param("post_id") long post_id);
    Optional<Post> findById(long post_id);
    List<OnlyPostMapping> findByPostId(long post_id);
//    OnlyPostMapping postById(long post_id);

    @Modifying
    @Transactional
    @Query(value = "update post set select_over = true where post_id = :post_id", nativeQuery = true)
    void changeSelectedReplyExist(@Param("post_id") long post_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE post SET views = views + 1 where post_id = :post_id", nativeQuery = true)
    int updateViewCnt(@Param("post_id") long post_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE post SET likes = likes + 1 where post_id = :post_id", nativeQuery = true)
    int updateLikeCntPlus(@Param("post_id") long post_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE post SET likes = likes - 1 where post_id = :post_id", nativeQuery = true)
    int updateLikeCntMinus(@Param("post_id") long post_id);

    @Modifying
    @Transactional
    @Query(value = "update post set reply_cnt = reply_cnt + 1 where post_id = :post_id", nativeQuery = true)
    int updateReplyCnt(@Param("post_id") long post_id);

    //한 카테고리 내 모든 포스트 통합 검색
//    @Query(value = "select distinct * from post where board_id = :board_id and (subject like concat('%',:keyword,'%') or content like concat('%',:keyword,'%'))", nativeQuery = true)
    List<OnlyPostMapping> findDistinctByBoard_BoardIdAndSubjectContainingOrBoard_BoardIdAndContentContaining(long board_id1, String keyword1, long board_id2, String keyword2);

    //한 카테고리 내 모든 포스트 제목 검색
    List<OnlyPostMapping> findByBoard_BoardIdAndSubjectContaining(long bord_id, String keyword);

    //한 카테고리 내 모든 포스트 내용 검색
    List<OnlyPostMapping> findByBoard_BoardIdAndContentContaining(long bord_id, String keyword);


    //한 블로그 내 모든 포스트 리스트
    List<OnlyPostMapping> findAllByWriter(String writer);

    //한 블로그 내 모든 포스트 통합 검색(or 블로그 내 모든 포스트 리스트)
//    @Query(value = "select distinct * from post where writer = :writer and (subject like concat('%',:keyword,'%') or content like concat('%',:keyword,'%'))", nativeQuery = true)
    List<OnlyPostMapping> findDistinctByWriterAndSubjectContainingOrWriterAndContentContaining(String writer1, String keyword1, String writer2, String keyword2);

    //한 블로그 내 모든 포스트 제목 검색
    List<OnlyPostMapping> findByWriterAndSubjectContaining(String writer, String keyword);

    //한 블로그 내 모든 포스트 내용 검색
    List<OnlyPostMapping> findByWriterAndContentContaining(String writer, String keyword);


    //사이트의 모든 포스트 리스트
    List<OnlyPostMapping> findAllByPostIdGreaterThan(long num);

    //사이트의 모든 포스트 통합 검색(or 사이트 내 모든 포스트 리스트)
//    @Query(value = "select distinct * from post where (subject like concat('%',:keyword,'%') or content like concat('%',:keyword,'%') or writer like concat('%',:keyword,'%'))", nativeQuery = true)
    List<OnlyPostMapping> findDistinctBySubjectContainingOrContentContainingOrWriterContaining(String keyword1, String keyword2, String keyword3);

    //사이트의 모든 포스트 제목 검색
    List<OnlyPostMapping> findBySubjectContaining(String keyword);

    //사이트의 모든 포스트 내용 검색
    List<OnlyPostMapping> findByContentContaining(String keyword);

    //사이트의 모든 포스트 작성자 검색
    List<OnlyPostMapping> findByWriterContaining(String keyword);
}
