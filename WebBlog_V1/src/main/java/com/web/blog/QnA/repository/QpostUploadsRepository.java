package com.web.blog.QnA.repository;

import com.web.blog.QnA.entity.QpostUploads;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QpostUploadsRepository extends JpaRepository<QpostUploads, Long> {
    List<QpostUploads> findAllByQpostId(long qpost_id); //한 사진에 업로드 된 모든 사진들 리스트

    Optional<List<QpostUploads>> findByQpostId(long qpost_id);

    void deleteByQpostId(long qpost_id);
}
