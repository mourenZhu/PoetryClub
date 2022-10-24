package cn.zhumouren.poetryclub.util;

import cn.zhumouren.poetryclub.properties.AppWebImageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    private static AppWebImageProperties appWebImageProperties;

    public static String getUserAvatarUrl(String avatarName) {
        return appWebImageProperties.getUrl() + appWebImageProperties.getUserAvatarPath() + "/" + avatarName;
    }

    public static String getUserAvatarName(String username) {
        return username + "_avatar";
    }

    @Autowired
    public void setAppWebImageProperties(AppWebImageProperties appWebImageProperties) {
        UserUtil.appWebImageProperties = appWebImageProperties;
    }

}
