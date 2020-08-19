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

    @Column(columnDefinition = "TEXT")
    private String fileName;

    private String nickname;

    private Long qpostId;

    private int num;

    @Builder
    public QpostUploads(Long id, String filePath, String fileName, String nickname, Long qpostId, int num) {
        this.id = id;
        this.filePath = filePath;
        this.fileName = fileName;
        this.nickname = nickname;
        this.qpostId = qpostId;
        this.num = num;
    }
}
