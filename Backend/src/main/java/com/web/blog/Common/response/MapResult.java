package com.web.blog.Common.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapResult<T1, T2> extends CommonResult {
    private T1 data1;
    private T2 data2;
}
