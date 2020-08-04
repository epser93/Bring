package com.web.blog.QnA.model;

public interface OnlyApostMapping {
    Long getApostId();
    String getWriter();
    String getAnswer();
    int getLikes();
    boolean getSelected();
}
