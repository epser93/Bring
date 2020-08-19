package com.web.blog.Board.repository;

import com.web.blog.Board.entity.PostUploads;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostUploadsRepository extends JpaRepository<PostUploads, Long> {
    List<PostUploads> findAllByPostId(long post_id); //한 사진에 업로드 된 모든 사진들 리스트

    Optional<List<PostUploads>> findByPostId(long post_id);

    void deleteByPostId(long post_id);
}
