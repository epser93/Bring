package com.web.blog.Board.model;

import java.time.LocalDateTime;

public interface OnlyReplyMapping {
    Long getReplyId();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getWriter();
    String getReply();
    int getLikes();
}
