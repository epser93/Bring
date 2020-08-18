package com.web.blog.Member.model;

import com.web.blog.Member.entity.ProfileImg;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProfileImgDto {
    private Long id;
    private String filePath;
    private String imgFullPath;
    private Long msrl;

    public ProfileImg toEntity() {
        ProfileImg build = ProfileImg.builder()
                .id(id)
                .filePath(filePath)
                .msrl(msrl)
                .build();
        return build;
    }

    @Builder
    public ProfileImgDto(Long id, String filePath, String imgFullPath, Long msrl) {
        this.id = id;
        this.filePath = filePath;
        this.imgFullPath = imgFullPath;
        this.msrl = msrl;
    }
}
