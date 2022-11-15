package cn.zhumouren.poetryclub.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.web.image")
public class AppWebImageProperty {
    private String url;
    private String userAvatarPath;
}
