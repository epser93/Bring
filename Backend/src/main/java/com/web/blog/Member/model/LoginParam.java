package com.web.blog.Member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Data
public class LoginParam {
    @NotBlank
    @Email
    @ApiModelProperty(value = "이메일")
    private String id;

    @NotBlank
    @Size(min = 7, max = 21, message = "비밀번호는 7자 이상 20자 이하여야 합니다.")
    @ApiModelProperty(value = "패스워드")
    private String password;
}
