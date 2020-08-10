package com.web.blog.Board.service;

import com.web.blog.Board.entity.PostUploads;
import com.web.blog.Board.model.PostUploadsDto;
import com.web.blog.Board.repository.PostUploadsRepository;
import com.web.blog.Common.service.S3Service;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PostUploadsService {
    private final PostUploadsRepository postUploadsRepository;
    private final S3Service s3Service;

    public PostUploads savePost(PostUploadsDto postUploadsDto) {
        return postUploadsRepository.save(postUploadsDto.toEntity());
    }

    public void deleteImgs(long postId) {
        postUploadsRepository.deleteByPostId(postId);
    }

    public List<PostUploadsDto> getList(long postId) {
        List<PostUploads> uploadsList = postUploadsRepository.findAllByPostId(postId);
        List<PostUploadsDto> postUploadsDtoList = new ArrayList<>();

        for (PostUploads uploads : uploadsList) {
            postUploadsDtoList.add(convertEntityToDto(uploads));
        }

        return postUploadsDtoList;
    }

    private PostUploadsDto convertEntityToDto(PostUploads uploads) {
        return PostUploadsDto.builder()
                .id(uploads.getId())
                .filePath(uploads.getFilePath())
                .postId(uploads.getPostId())
                .imgFullPath("https://" + s3Service.CLOUD_FRONT_DOMAIN_NAME + "/" + uploads.getFilePath())
                .build();
    }
}
