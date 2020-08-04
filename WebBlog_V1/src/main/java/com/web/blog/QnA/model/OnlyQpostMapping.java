package com.web.blog.QnA.model;

import java.time.LocalDateTime;

public interface OnlyQpostMapping {
    Long getQpostId();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getWriter();
    String getSubject();
    String getContent();
    int getViews();
    int getAnswerCnt();
    boolean getSelectOver();
}
