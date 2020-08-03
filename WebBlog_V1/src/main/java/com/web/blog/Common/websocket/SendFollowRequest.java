package com.web.blog.Common.websocket;

import com.web.blog.Member.entity.FollowRequest;
import com.web.blog.Member.service.FollowRequestService;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Getter
public class SendFollowRequest {
    private Set<WebSocketSession> sessions = new HashSet<>();

    public void handleActions(WebSocketSession session, FollowRequest followRequest, FollowRequestService followRequestService) {
        if(followRequest.getType().equals(FollowRequest.ResponseType.REQUEST)) {
            sessions.add(session);
            followRequest.setMessage(followRequest.getSender() + "님이 팔로우 요청을 보냈습니다.");
        }
        sendMessage(followRequest, followRequestService);
    }

    public <T> void sendMessage(T message, FollowRequestService followRequestService) {
        sessions.parallelStream().forEach(session -> followRequestService.sendMessage(session, message));
    }
}
