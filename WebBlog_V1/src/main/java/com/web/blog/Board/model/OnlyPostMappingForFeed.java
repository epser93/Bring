package com.web.blog.Board.model;


import java.time.LocalDateTime;

public interface OnlyPostMappingForFeed {
    Long getPostId();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String getMember_nickname();

    String getSubject();

    String getContent();

    int getViews();

    int getLikes();

    int getReplyCnt();

    Long getOriginal();

    String getMember_uid();

//    Set<String> getTags();
}
