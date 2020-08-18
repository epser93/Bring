package com.web.blog.Board.model;

import java.time.LocalDateTime;

public interface OnlyReplyMapping {
    Long getReplyId();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String getMember_nickname();

    String getReply();

    int getLikes();

    long getPost_postId();
}
