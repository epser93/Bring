package com.web.blog.Member.model;

import java.time.LocalDateTime;

public interface OnlyMemberMapping {
    Long getMsrl();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String getUid();

    String getName();

    String getNickname();

    String getTechStack();

    int getLikedpost();

    int getScore();

    int getFollowersCnt();

    int getFollowingCnt();
}
