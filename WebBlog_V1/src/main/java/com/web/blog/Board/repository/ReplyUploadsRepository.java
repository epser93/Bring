package com.web.blog.Board.repository;

import com.web.blog.Board.entity.ReplyUploads;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyUploadsRepository extends JpaRepository<ReplyUploads, Long> {
    List<ReplyUploads> findAllByReplyId(long reply_id); //한 사진에 업로드 된 모든 사진들 리스트

    Optional<ReplyUploads> findByReplyId(long reply_id);

    void deleteByReplyId(long reply_id);
}

