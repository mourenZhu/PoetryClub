package cn.zhumouren.poetryclub.utils;

import cn.zhumouren.poetryclub.properties.AppWebImageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    private static AppWebImageProperties appWebImageProperties;

    @Autowired
    public void setAppWebImageProperties(AppWebImageProperties appWebImageProperties) {
        UserUtil.appWebImageProperties = appWebImageProperties;
    }

    public static String getUserAvatarUrl(String username) {
        return appWebImageProperties.getUrl() + appWebImageProperties.getUserAvatarPath() + "/"
                + getUserAvatarName(username) + ".jpg";
    }

    public static String getUserAvatarName(String username) {
        return username + "_avatar";
    }

}
