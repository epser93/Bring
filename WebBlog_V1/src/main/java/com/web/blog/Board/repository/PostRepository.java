package com.web.blog.Board.repository;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBoard(Board board);
    Page<Post> findByBoard(Board board, Pageable pageable);
    Page<Post> findByBoard_BoardId(long board_id, Pageable pageable);

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

    //한 카테고리 내 모든 포스트 리스트
    List<Post> findByBoard_BoardId(long board_id);

    //한 카테고리 내 모든 포스트 통합 검색
    @Query(value = "select distinct * from post where board_id = :board_id and (subject like concat('%',:keyword,'%') or content like concat('%',:keyword,'%') or tag like concat('%',:keyword,'%'))", nativeQuery = true)
    List<Post> searchCategoryPosts(@Param("board_id") long board_id, @Param("keyword") String keyword);

    //한 카테고리 내 모든 포스트 제목 검색
    List<Post> findByBoard_BoardIdAndSubjectContaining(long bord_id, String keyword);

    //한 카테고리 내 모든 포스트 내용 검색
    List<Post> findByBoard_BoardIdAndContentContaining(long bord_id, String keyword);

    //한 카테고리 내 모든 포스트 태그 검색
    List<Post> findByBoard_BoardIdAndTagContaining(long bord_id, String keyword);


    //한 블로그 내 모든 포스트 리스트
    List<Post> findByWriter(String writer);

    //한 블로그 내 모든 포스트 통합 검색(or 블로그 내 모든 포스트 리스트)
    @Query(value = "select distinct * from post where writer = :writer and (subject like concat('%',:keyword,'%') or content like concat('%',:keyword,'%') or tag like concat('%',:keyword,'%'))", nativeQuery = true)
    List<Post> searchBlogPosts(@Param("writer") String writer, @Param("keyword") String keyword);

    //한 블로그 내 모든 포스트 제목 검색
    List<Post> findByWriterAndSubjectContaining(String writer, String keyword);

    //한 블로그 내 모든 포스트 내용 검색
    List<Post> findByWriterAndContentContaining(String writer, String keyword);

    //한 블로그 내 모든 포스트 태그 검색
    List<Post> findByWriterAndTagContaining(String writer, String keyword);


    //사이트의 모든 포스트 리스트
    List<Post> findAll();

    //사이트의 모든 포스트 통합 검색(or 사이트 내 모든 포스트 리스트)
    @Query(value = "select distinct * from post where (subject like concat('%',:keyword,'%') or content like concat('%',:keyword,'%') or tag like concat('%',:keyword,'%') or writer like concat('%',:keyword,'%'))", nativeQuery = true)
    List<Post> searchEveryBlogPosts(@Param("keyword") String keyword);

    //사이트의 모든 포스트 제목 검색
    List<Post> findBySubjectContaining(String keyword);

    //사이트의 모든 포스트 내용 검색
    List<Post> findByContentContaining(String keyword);

    //사이트의 모든 포스트 태그 검색
    List<Post> findByTagContaining(String keyword);

    //사이트의 모든 포스트 작성자 검색
    List<Post> findByWriterContaining(String keyword);
}
