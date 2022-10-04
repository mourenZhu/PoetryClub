package cn.zhumouren.poetryclub.config;

import cn.zhumouren.poetryclub.constants.RoleType;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

import static org.springframework.messaging.simp.SimpMessageType.MESSAGE;
import static org.springframework.messaging.simp.SimpMessageType.SUBSCRIBE;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/25 9:23
 **/
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpSubscribeDestMatchers("/user/queue/errors").permitAll()
                .simpDestMatchers("/app/**").hasRole(RoleType.USER.getStr())
                .simpSubscribeDestMatchers("/user/**", "/topic/friends/*").hasRole(RoleType.USER.getStr())
                .simpTypeMatchers(MESSAGE, SUBSCRIBE).denyAll()
                .anyMessage().denyAll();
    }



    // 禁用CSRF
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
