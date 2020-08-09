package com.web.blog.Board.model;

import com.web.blog.Board.entity.PostUploads;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PostUploadsDto {
    private Long id;
    private String filePath;
    private String imgFullPath;
    private Long postId;
    private int num;

    public PostUploads toEntity(){
        PostUploads build = PostUploads.builder()
                .id(id)
                .filePath(filePath)
                .postId(postId)
                .num(num)
                .build();
        return build;
    }

    @Builder
    public PostUploadsDto(Long id, String filePath, String imgFullPath, Long postId, int num) {
        this.id = id;
        this.filePath = filePath;
        this.imgFullPath = imgFullPath;
        this.postId = postId;
        this.num = num;
    }
}