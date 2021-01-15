package com.web.blog.QnA.repository;

import com.web.blog.QnA.entity.QpostUploads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface QpostUploadsRepository extends JpaRepository<QpostUploads, Long> {
    List<QpostUploads> findAllByQpostId(long qpost_id); //한 사진에 업로드 된 모든 사진들 리스트

    List<QpostUploads> findAllByNickname(String nickname);

    Optional<List<QpostUploads>> findByQpostId(long qpost_id);

    void deleteByQpostId(long qpost_id);

    @Modifying
    @Transactional
    @Query(value = "update qpost_uploads set file_path = :file_path where id = :id", nativeQuery = true)
    int updateFilePath(@Param("file_path") String file_path, @Param("id") long id);

    @Modifying
    @Transactional
    @Query(value = "update qpost_uploads set qpost_id = :qpost_id where id = :id", nativeQuery = true)
    int updateQpostId(@Param("qpost_id") long qpost_id, @Param("id") long id);
}
