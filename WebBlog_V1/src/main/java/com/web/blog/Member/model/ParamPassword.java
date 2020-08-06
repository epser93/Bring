package com.web.blog.Member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ParamPassword {
    @Size(min = 0, max = 21, message = "비밀번호는 7자 이상 20자 이하여야 합니다.")
    @ApiModelProperty(value = "새 비밀번호")
    private String password1;

    @ApiModelProperty(value = "새 비밀번호 확인")
    private String password2;

    @NotBlank
    @ApiModelProperty(value = "기존 비밀번호")
    private String password3;

    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "프로필 이미지")
    private MultipartFile uploadFile;
}

