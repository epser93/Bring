package com.web.blog.QnA.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ParamApost {
    @ApiModelProperty(value = "내용")
    private String answer;

    @Size(max=1000)
    @ApiModelProperty(value = "태그")
    private String tag;
}
