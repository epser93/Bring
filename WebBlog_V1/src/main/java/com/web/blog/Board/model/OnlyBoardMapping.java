package com.web.blog.Board.model;

public interface OnlyBoardMapping {
    long getBoardId();
    String getName();
    long getMember_msrl();
    long getMember_nickname();
    int getPostCnt();
}
