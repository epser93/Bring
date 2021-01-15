package com.web.blog.Board.repository;

import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.PostMember;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface PostMemberRepository extends CrudRepository<PostMember, Long> {
    Optional<PostMember> findPostMemberByMember_MsrlAndPost(long msrl, Post post);

    Optional<PostMember> findPostMemberByMember_MsrlAndPost_PostId(long msrl, long postId);

    @Modifying
    @Transactional
    @Query(value = "insert into post_member (msrl, post_id) values (:msrl, :post_id)", nativeQuery = true)
    int insertLike(@Param("msrl") long msrl, @Param("post_id") long post_id);

    @Modifying
    @Transactional
    @Query(value = "delete from post_member where msrl = :msrl and post_id = :post_id", nativeQuery = true)
    int deleteLike(@Param("msrl") long msrl, @Param("post_id") long post_id);

    //멤버가 좋아요한 게시글 수
    @Transactional
    @Query(value = "select count(post_id) from post_member where msrl = :msrl", nativeQuery = true)
    int likedPostCnt(@Param("msrl") long msrl);

    //게시글 좋아요 수
    @Transactional
    @Query(value = "select count(msrl) from post_member where post_id = :post_id", nativeQuery = true)
    int likedMemberCnt(@Param("post_id") long post_id);

    //멤바가 좋아요 한 게시글 리스트
    @Transactional
    @Query(value = "select post_id from post_member where msrl = :msrl", nativeQuery = true)
    List<Long> likedPost(@Param("msrl") long msrl);

    //게시글 좋아요 누른 유저 리스트
    @Transactional
    @Query(value = "select msrl from post_member where post_id = :post_id", nativeQuery = true)
    List<Long> likedMember(@Param("post_id") long post_id);

    List<PostMember> findPostMemberByPost(Post post);
}
