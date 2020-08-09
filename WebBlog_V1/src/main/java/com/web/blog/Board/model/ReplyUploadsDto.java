package com.web.blog.Board.model;

import com.web.blog.Board.entity.ReplyUploads;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReplyUploadsDto {
    private Long id;
    private String filePath;
    private String imgFullPath;
    private Long replyId;
    private int num;

    public ReplyUploads toEntity(){
        ReplyUploads build = ReplyUploads.builder()
                .id(id)
                .filePath(filePath)
                .replyId(replyId)
                .num(num)
                .build();
        return build;
    }

    @Builder
    public ReplyUploadsDto(Long id, String filePath, String imgFullPath, Long replyId, int num) {
        this.id = id;
        this.filePath = filePath;
        this.imgFullPath = imgFullPath;
        this.replyId = replyId;
        this.num = num;
    }
}