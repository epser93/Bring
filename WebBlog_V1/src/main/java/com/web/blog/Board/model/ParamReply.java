package com.web.blog.Board.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParamReply {
    @ApiModelProperty(value = "댓글")
    private String reply;
}
