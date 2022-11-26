package cn.zhumouren.poetryclub.listener;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.service.UserWebsocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.util.List;

@Component
@Slf4j
public class WebSocketListener {

    private final SimpMessagingTemplate template;

    private final List<UserWebsocketService> userWebsocketServiceList;

    public WebSocketListener(SimpMessagingTemplate template, List<UserWebsocketService> userWebsocketServiceList) {
        this.template = template;
        this.userWebsocketServiceList = userWebsocketServiceList;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent sessionConnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionConnectEvent.getMessage());
        log.debug("connect, sessionId = {}, username = {}",
                headerAccessor.getSessionId(), headerAccessor.getUser().getName());
    }

    @EventListener
    public void handleWebSocketConnectedListener(SessionConnectedEvent sessionConnectedEvent) {
        log.debug("connected, connected username = {}", sessionConnectedEvent.getUser().getName());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent sessionDisconnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) headerAccessor.getUser();
        userWebsocketServiceList.forEach(userWebsocketService ->
                userWebsocketService.disconnect((UserEntity) authenticationToken.getPrincipal()));
        log.debug("disconnect, session id = {}, username = {}", sessionDisconnectEvent.getSessionId(),
                headerAccessor.getUser().getName());
    }

    @EventListener
    public void handleWebSocketSubscribeEvent(SessionSubscribeEvent sessionSubscribeEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionSubscribeEvent.getMessage());
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) headerAccessor.getUser();
        userWebsocketServiceList.forEach(userWebsocketService ->
                userWebsocketService.subscribe(headerAccessor.getDestination(),
                        (UserEntity) authenticationToken.getPrincipal()));
        log.debug("subscribe, sessionId = {}, username = {}",
                headerAccessor.getSessionId(), headerAccessor.getUser().getName());
    }

    @EventListener
    public void handleWebSocketUnsubscribeEvent(SessionUnsubscribeEvent sessionUnsubscribeEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionUnsubscribeEvent.getMessage());
        log.debug("unsubscribe, sessionId = {}, username = {}",
                headerAccessor.getSessionId(), headerAccessor.getUser().getName());
    }

}
