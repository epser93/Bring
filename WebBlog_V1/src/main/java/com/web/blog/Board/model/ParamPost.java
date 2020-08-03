package com.web.blog.Board.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ParamPost {
    @NotEmpty
    @Size(min = 2, max = 100)
    @ApiModelProperty(value = "제목")
    private String subject;

    @ApiModelProperty(value = "내용")
    private String content;

    @Size(max = 1000)
    @ApiModelProperty(value = "태그")
    private String tag;
}
