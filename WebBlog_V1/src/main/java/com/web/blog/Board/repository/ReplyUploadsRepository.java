package com.web.blog.Board.repository;

import com.web.blog.Board.entity.ReplyUploads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ReplyUploadsRepository extends JpaRepository<ReplyUploads, Long> {
    List<ReplyUploads> findAllByReplyId(long reply_id); //한 사진에 업로드 된 모든 사진들 리스트

    List<ReplyUploads> findAllByNickname(String nickname);

    Optional<List<ReplyUploads>> findByReplyId(long reply_id);

    void deleteByReplyId(long reply_id);

    @Modifying
    @Transactional
    @Query(value = "update reply_uploads set file_path = :file_path where id = :id", nativeQuery = true)
    int updateFilePath(@Param("file_path") String file_path, @Param("id") long id);

    @Modifying
    @Transactional
    @Query(value = "update reply_uploads set reply_id = :reply_id where id = :id", nativeQuery = true)
    int updateReplyId(@Param("reply_id") long reply_id, @Param("id") long id);
}

