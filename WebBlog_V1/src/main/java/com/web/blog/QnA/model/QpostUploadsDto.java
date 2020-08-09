package com.web.blog.QnA.model;

import com.web.blog.QnA.entity.QpostUploads;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class QpostUploadsDto {
    private Long id;
    private String filePath;
    private String imgFullPath;
    private Long qpostId;
    private int num;

    public QpostUploads toEntity() {
        QpostUploads build = QpostUploads.builder()
                .id(id)
                .filePath(filePath)
                .qpostId(qpostId)
                .num(num)
                .build();
        return build;
    }

    @Builder
    public QpostUploadsDto(Long id, String filePath, String imgFullPath, Long qpostId, int num) {
        this.id = id;
        this.filePath = filePath;
        this.imgFullPath = imgFullPath;
        this.qpostId = qpostId;
        this.num = num;
    }
}