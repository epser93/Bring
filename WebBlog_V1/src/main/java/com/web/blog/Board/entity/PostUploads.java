package com.web.blog.Board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class PostUploads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String filePath;

    private Long postId;

    private int num;

    @Builder
    public PostUploads(Long id, String filePath, Long postId, int num) {
        this.id = id;
        this.filePath = filePath;
        this.postId = postId;
        this.num = num;
    }
}
