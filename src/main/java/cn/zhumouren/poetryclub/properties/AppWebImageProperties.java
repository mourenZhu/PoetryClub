package cn.zhumouren.poetryclub.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.web.image")
public class AppWebImageProperties {
    private String url;
    private String userAvatarPath;
}
