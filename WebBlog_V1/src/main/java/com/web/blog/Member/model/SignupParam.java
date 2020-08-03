package com.web.blog.Member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

@NoArgsConstructor
@Data
public class SignupParam {
    @NotBlank
    @Email
    @ApiModelProperty(value = "이메일")
    private String uid;

    @NotBlank
    @Size(min = 7, max = 21, message = "비밀번호는 7자 이상 20자 이하여야 합니다.")
    @ApiModelProperty(value = "비밀번호")
    private String password1;

    @NotBlank
    @Size(min = 7, max = 21)
    @ApiModelProperty(value = "비밀번호 확인")
    private String password2;

    @NotBlank
    @Size(min = 1, max = 6, message = "이름은 2자 이상 5자 이하여야 합니다.")
    @ApiModelProperty(value = "이름")
    private String name;

    @NotBlank
    @Size(min = 3, max = 16, message = "닉네임은 3자 이상 15자 이하여야 합니다.")
    @ApiModelProperty(value = "닉네임")
    private String nickname;
}
