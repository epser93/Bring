package com.web.blog.QnA.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParamApost {
    @ApiModelProperty(value = "내용")
    private String answer;
}
