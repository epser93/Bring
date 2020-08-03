package com.web.blog.Common.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.blog.Member.entity.FollowRequest;
import com.web.blog.Member.service.FollowRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class CWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final FollowRequestService followRequestService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);
        FollowRequest followRequest = objectMapper.readValue(payload, FollowRequest.class);
        SendFollowRequest request = new SendFollowRequest();
        request.handleActions(session, followRequest, followRequestService);
    }
}