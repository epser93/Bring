package com.web.blog.QnA.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class QpostUploads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String filePath;

    private Long qpostId;

    private int num;

    @Builder
    public QpostUploads(Long id, String filePath, Long qpostId, int num) {
        this.id = id;
        this.filePath = filePath;
        this.qpostId = qpostId;
        this.num = num;
    }
}
