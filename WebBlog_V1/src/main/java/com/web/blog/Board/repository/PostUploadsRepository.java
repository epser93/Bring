package com.web.blog.Board.repository;

import com.web.blog.Board.entity.PostUploads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface PostUploadsRepository extends JpaRepository<PostUploads, Long> {
    List<PostUploads> findAllByPostId(long post_id); //한 사진에 업로드 된 모든 사진들 리스트

    List<PostUploads> findAllByNickname(String nickname);

    Optional<List<PostUploads>> findByPostId(long post_id);

    void deleteByPostId(long post_id);

    @Modifying
    @Transactional
    @Query(value = "update post_uploads set file_path = :file_path where id = :id", nativeQuery = true)
    int updateFilePath(@Param("file_path") String file_path, @Param("id") long id);

    @Modifying
    @Transactional
    @Query(value = "update post_uploads set post_id = :post_id where id = :id", nativeQuery = true)
    int updatePostId(@Param("post_id") long post_id, @Param("id") long id);
}
