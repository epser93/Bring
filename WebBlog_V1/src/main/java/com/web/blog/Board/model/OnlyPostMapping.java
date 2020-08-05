package com.web.blog.Board.model;


import com.web.blog.Board.entity.Board;

import java.time.LocalDateTime;

public interface OnlyPostMapping {
    Long getPostId();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getWriter();
    String getSubject();
    String getContent();
    int getViews();
    int getLikes();
    String getBoard_name();
}
