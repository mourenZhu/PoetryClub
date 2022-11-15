package cn.zhumouren.poetryclub.property;

import cn.zhumouren.poetryclub.config.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/15 19:41
 **/
@Component
@Data
@PropertySource(value = "classpath:privacy/oauth2-github.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "oauth2-github")
public class GitHubOauth2Property {
    private String clientId;
    private String clientSecret;
    private String authorizeUrl;
    private String redirectUrl;
    private String accessTokenUrl;
    private String userInfoUrl;
}
