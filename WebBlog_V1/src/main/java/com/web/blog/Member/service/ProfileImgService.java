package com.web.blog.Member.service;

import com.web.blog.Common.advice.exception.CResourceNotExistException;
import com.web.blog.Common.service.S3Service;
import com.web.blog.Member.entity.ProfileImg;
import com.web.blog.Member.model.ProfileImgDto;
import com.web.blog.Member.repository.ProfileImgRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProfileImgService {
    private final ProfileImgRepository profileImgRepository;
    private final S3Service s3Service;

    public ProfileImg savePost(ProfileImgDto profileImgDto) {
        if(profileImgRepository.findByMsrl(profileImgDto.getMsrl()).isPresent()) {
            profileImgRepository.deleteByMsrl(profileImgDto.getMsrl());
        }
        return profileImgRepository.save(profileImgDto.toEntity());
    }

    public ProfileImgDto getOneImg(long msrl) {
        ProfileImg profileImg = profileImgRepository.findByMsrl(msrl).orElseThrow(CResourceNotExistException::new);
        ProfileImgDto profileImgDto = convertEntityToDto(profileImg);
        return profileImgDto;
    }

    private ProfileImgDto convertEntityToDto(ProfileImg profileImg) {
        return ProfileImgDto.builder()
                .id(profileImg.getId())
                .filePath(profileImg.getFilePath())
                .msrl(profileImg.getMsrl())
                .imgFullPath("https://" + s3Service.CLOUD_FRONT_DOMAIN_NAME + "/" + profileImg.getFilePath())
                .build();
    }
}
