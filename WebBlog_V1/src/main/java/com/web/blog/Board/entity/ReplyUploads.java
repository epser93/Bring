package com.web.blog.Board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ReplyUploads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String filePath;

    private Long replyId;

    private int num;

    @Builder
    public ReplyUploads(Long id, String filePath, Long replyId, int num) {
        this.id = id;
        this.filePath = filePath;
        this.replyId = replyId;
        this.num = num;
    }
}
