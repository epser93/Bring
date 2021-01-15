package com.web.blog.Board.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParamSearch {
    @ApiModelProperty(value = "제목")
    private String subject;

    @ApiModelProperty(value = "컨텐츠")
    private String content;

    @ApiModelProperty(value = "태그")
    private String tag;

    @ApiModelProperty(value = "작성자")
    private String writer;

    @ApiModelProperty(value = "좋아요 수")
    private int likes;

    @ApiModelProperty(value = "조회수")
    private int views;
}
