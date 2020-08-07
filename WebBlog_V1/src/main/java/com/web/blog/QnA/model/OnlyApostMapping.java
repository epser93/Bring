package com.web.blog.QnA.model;

import com.web.blog.QnA.entity.Qpost;

public interface OnlyApostMapping {
    Long getApostId();
    String getWriter();
    String getAnswer();
    int getLikes();
    boolean getSelected();
    Long getPostId();
}
