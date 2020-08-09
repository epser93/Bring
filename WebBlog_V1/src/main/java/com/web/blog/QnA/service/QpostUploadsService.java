package com.web.blog.QnA.service;

import com.web.blog.Common.service.S3Service;
import com.web.blog.QnA.entity.QpostUploads;
import com.web.blog.QnA.model.QpostUploadsDto;
import com.web.blog.QnA.repository.QpostUploadsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class QpostUploadsService {
    private final QpostUploadsRepository qpostUploadsRepository;
    private final S3Service s3Service;

    public QpostUploads savePost(QpostUploadsDto qpostUploadsDto) {
        return qpostUploadsRepository.save(qpostUploadsDto.toEntity());
    }

    public void deleteImgs(long qpostId) {
        qpostUploadsRepository.deleteByQpostId(qpostId);
    }

    public List<QpostUploadsDto> getList(long qpostId) {
        List<QpostUploads> uploadsList = qpostUploadsRepository.findAllByQpostId(qpostId);
        List<QpostUploadsDto> qpostUploadsDtoList = new ArrayList<>();

        for (QpostUploads uploads : uploadsList) {
            qpostUploadsDtoList.add(convertEntityToDto(uploads));
        }

        return qpostUploadsDtoList;
    }

    private QpostUploadsDto convertEntityToDto(QpostUploads uploads) {
        return QpostUploadsDto.builder()
                .id(uploads.getId())
                .filePath(uploads.getFilePath())
                .qpostId(uploads.getQpostId())
                .imgFullPath("https://" + s3Service.CLOUD_FRONT_DOMAIN_NAME + "/qposts/" + uploads.getFilePath())
                .build();
    }
}
