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
public class ParamBoard {
    @NotEmpty
    @Size(min = 1, max = 100)
    @ApiModelProperty(value = "카테고리")
    private String name;
}
