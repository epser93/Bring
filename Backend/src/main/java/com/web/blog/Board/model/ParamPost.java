package com.web.blog.Board.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ParamPost {
    @NotEmpty
    @ApiModelProperty(value = "제목")
    private String subject;

    @ApiModelProperty(value = "내용")
    private String content;

    @ApiModelProperty(value = "태그")
    private Set<String> tags;

    @ApiModelProperty(value = "공유된 게시글 여부")
    private Long original;
}
