package cn.zhumouren.poetryclub.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/11 16:18
 **/
@Data
@Component
@ConfigurationProperties(prefix = "app.init")
public class AppInitProperty {
    private String poemFilesPath;
}
