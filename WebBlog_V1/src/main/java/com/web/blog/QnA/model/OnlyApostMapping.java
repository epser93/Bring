package com.web.blog.QnA.model;

import java.time.LocalDateTime;

public interface OnlyApostMapping {
    Long getApostId();
    String getMember_nickname();
    String getAnswer();
    int getLikes();
    boolean getSelected();
    Long getPostId();
    LocalDateTime getCreatedAt();
}
