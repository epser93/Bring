package com.web.blog.Member.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "profileImg")
public class ProfileImg {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String filePath;

    private Long msrl;

    @Builder
    public ProfileImg(Long id, String filePath, Long msrl) {
        this.id = id;
        this.filePath = filePath;
        this.msrl = msrl;
    }
}
