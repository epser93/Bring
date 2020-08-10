package com.web.blog.Board.service;

import com.web.blog.Board.entity.ReplyUploads;
import com.web.blog.Board.model.ReplyUploadsDto;
import com.web.blog.Board.repository.ReplyUploadsRepository;
import com.web.blog.Common.service.S3Service;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReplyUploadsService {
    private final ReplyUploadsRepository replyUploadsRepository;
    private final S3Service s3Service;

    public ReplyUploads savePost(ReplyUploadsDto replyUploadsDto) {
        return replyUploadsRepository.save(replyUploadsDto.toEntity());
    }

    public void deleteImgs(long replyId) {
        replyUploadsRepository.deleteByReplyId(replyId);
    }

    public List<ReplyUploadsDto> getList(long replyId) {
        List<ReplyUploads> uploadsList = replyUploadsRepository.findAllByReplyId(replyId);
        List<ReplyUploadsDto> replyUploadsDtoList = new ArrayList<>();

        for (ReplyUploads uploads : uploadsList) {
            replyUploadsDtoList.add(convertEntityToDto(uploads));
        }

        return replyUploadsDtoList;
    }

    private ReplyUploadsDto convertEntityToDto(ReplyUploads uploads) {
        return ReplyUploadsDto.builder()
                .id(uploads.getId())
                .filePath(uploads.getFilePath())
                .replyId(uploads.getReplyId())
                .imgFullPath("https://" + s3Service.CLOUD_FRONT_DOMAIN_NAME + "/" + uploads.getFilePath())
                .build();
    }
}
