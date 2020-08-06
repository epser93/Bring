package com.web.blog.Board.model;


import java.time.LocalDateTime;
import java.util.Set;

public interface OnlyPostMapping {
    Long getPostId();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String getWriter();

    String getSubject();

    String getContent();

    int getViews();

    int getLikes();

    int getReplyCnt();

    String getBoard_name();

    Long getOriginal();

    Set<String> getTags();
}
