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
    private String fileName;//
    private String nickname;//
    private String imgFullPath;
    private Long postId;
    private int num;

    public PostUploads toEntity() {
        PostUploads build = PostUploads.builder()
                .id(id)
                .filePath(filePath)
                .fileName(fileName)
                .nickname(nickname)
                .postId(postId)
                .num(num)
                .build();
        return build;
    }

    @Builder
    public PostUploadsDto(Long id, String filePath, String fileName, String nickname, String imgFullPath, Long postId, int num) {
        this.id = id;
        this.filePath = filePath;
        this.fileName = fileName;
        this.nickname = nickname;
        this.imgFullPath = imgFullPath;
        this.postId = postId;
        this.num = num;
    }
}