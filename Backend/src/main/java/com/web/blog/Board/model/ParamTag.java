package com.web.blog.Board.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParamTag {
    @ApiModelProperty(value = "태그")
    private String tag;
}
