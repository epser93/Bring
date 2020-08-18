package com.web.blog.Board.repository;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.entity.Post;
import com.web.blog.Board.model.OnlyPostMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBoard(Board board);

    List<OnlyPostMapping> findAllByOrderByPostIdAsc();

    List<OnlyPostMapping> findAllByBoard_BoardIdOrderByPostIdDesc(long board_id, Pageable pageable);

    List<OnlyPostMapping> findByPostId(long post_id);

    Optional<OnlyPostMapping> findAllByPostId(long post_id);

    Optional<List<Post>> findByBoard_BoardId(long boardId);

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

    @Modifying
    @Transactional
    @Query(value = "update post set reply_cnt = reply_cnt - 1 where post_id = :post_id", nativeQuery = true)
    int updateReplyCntMinus(@Param("post_id") long post_id);

    //한 카테고리 내 모든 포스트 통합 검색
//    @Query(value = "select distinct * from post where board_id = :board_id and (subject like concat('%',:keyword,'%') or content like concat('%',:keyword,'%'))", nativeQuery = true)
    List<OnlyPostMapping> findDistinctByBoard_BoardIdAndSubjectContainingAndBoard_NameNotLikeOrBoard_BoardIdAndContentContainingAndBoard_NameNotLikeOrderByPostIdDesc(long board_id1, String keyword1, String notlike1, long board_id2, String keyword2, String notlike2, Pageable pageable);

    //한 카테고리 내 모든 포스트 제목 검색
    List<OnlyPostMapping> findByBoard_BoardIdAndSubjectContainingAndBoard_NameNotLikeOrderByPostIdDesc(long bord_id, String keyword, String notlike, Pageable pageable);

    //한 카테고리 내 모든 포스트 내용 검색
    List<OnlyPostMapping> findByBoard_BoardIdAndContentContainingAndBoard_NameNotLikeOrderByPostIdDesc(long bord_id, String keyword, String notlike, Pageable pageable);


    //한 블로그 내 모든 포스트 리스트
    List<OnlyPostMapping> findAllByMember_NicknameAndBoard_NameNotLikeOrderByPostIdDesc(String writer, String notlike, Pageable pageable);

    List<OnlyPostMapping> findAllByMember_NicknameAndBoard_NameNotLikeAndCreatedAtLessThanEqualOrderByCreatedAtAsc(String writer, String notlike, LocalDateTime date, Pageable pageable);

    List<Post> findAllByMember_NicknameAndBoard_NameNotLikeAndCreatedAtLessThanEqualOrderByCreatedAtDesc(String writer, String notlike, LocalDateTime date);

    List<OnlyPostMapping> findAllByMember_NicknameAndBoard_NameNotLike(String writer, String notlike);

    List<OnlyPostMapping> findAllByMember_Nickname(String writer);

    //한 블로그 내 모든 포스트 통합 검색(or 블로그 내 모든 포스트 리스트)
//    @Query(value = "select distinct * from post where nickname = :nickname and (subject like concat('%',:keyword,'%') or content like concat('%',:keyword,'%'))", nativeQuery = true)
    List<OnlyPostMapping> findDistinctByMember_NicknameAndSubjectContainingAndBoard_NameNotLikeOrMember_NicknameAndContentContainingAndBoard_NameNotLikeOrderByPostIdDesc(String writer1, String keyword1, String notlike1, String writer2, String keyword2, String notlike2, Pageable pageable);

    //한 블로그 내 모든 포스트 제목 검색
    List<OnlyPostMapping> findByMember_NicknameAndSubjectContainingAndBoard_NameNotLikeOrderByPostIdDesc(String writer, String keyword, String notlike, Pageable pageable);

    //한 블로그 내 모든 포스트 내용 검색
    List<OnlyPostMapping> findByMember_NicknameAndContentContainingAndBoard_NameNotLikeOrderByPostIdDesc(String writer, String keyword, String notlike, Pageable pageable);


    //사이트의 모든 포스트 리스트(최신글)
    List<OnlyPostMapping> findByCreatedAtLessThanEqualAndBoard_NameNotLikeOrderByCreatedAtDesc(LocalDateTime date, String notlike, Pageable pageable);

    //사이트의 모든 포스트 리스트(인기글)
    List<OnlyPostMapping> findDistinctByViewsGreaterThanEqualAndCreatedAtLessThanEqualAndBoard_NameNotLikeOrLikesGreaterThanEqualAndCreatedAtLessThanEqualAndBoard_NameNotLikeOrderByCreatedAtDesc(int views, LocalDateTime dateV, String notlike1, int likes, LocalDateTime dateL, String notlike2, Pageable pageable);


    //사이트의 모든 포스트 통합 검색(or 사이트 내 모든 포스트 리스트)
//    @Query(value = "select distinct * from post where (subject like concat('%',:keyword,'%') or content like concat('%',:keyword,'%') or writer like concat('%',:keyword,'%'))", nativeQuery = true)
    List<OnlyPostMapping> findDistinctBySubjectContainingAndBoard_NameNotLikeOrContentContainingAndBoard_NameNotLikeOrMember_NicknameContainingAndBoard_NameNotLikeOrderByPostIdDesc(String keyword1, String notlike1, String keyword2, String notlike2, String keyword3, String notlike3, Pageable pageable);

    //사이트의 모든 포스트 제목 검색
    List<OnlyPostMapping> findBySubjectContainingAndBoard_NameNotLikeOrderByPostIdDesc(String keyword, String notlike, Pageable pageable);

    //사이트의 모든 포스트 내용 검색
    List<OnlyPostMapping> findByContentContainingAndBoard_NameNotLikeOrderByPostIdDesc(String keyword, String notlike, Pageable pageable);

    //사이트의 모든 포스트 작성자 검색
    List<OnlyPostMapping> findByMember_NicknameContainingAndBoard_NameNotLikeOrderByPostIdDesc(String keyword, String notlike, Pageable pageable);
}
