package com.web.blog.Member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ParamMember {
    @NotBlank
    @Size(min = 7, max = 21, message = "비밀번호는 7자 이상 20자 이하여야 합니다.")
    @ApiModelProperty(value = "비밀번호", required = true)
    private String password1;

    @NotBlank
    @Size(min = 1, max = 100)
    @ApiModelProperty(value = "비밀번호 확인")
    private String password2;

    @NotBlank
    @Size(min = 3, max = 16)
    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "프로필 이미지")
    private MultipartFile uploadFile;
}

