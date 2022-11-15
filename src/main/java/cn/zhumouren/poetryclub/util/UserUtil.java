package cn.zhumouren.poetryclub.util;

import cn.zhumouren.poetryclub.property.AppWebImageProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    private static AppWebImageProperty appWebImageProperty;

    public static String getUserAvatarUrl(String avatarName) {
        return appWebImageProperty.getUrl() + appWebImageProperty.getUserAvatarPath() + "/" + avatarName;
    }

    public static String getUserAvatarName(String username) {
        return username + "_avatar";
    }

    @Autowired
    public void setAppWebImageProperties(AppWebImageProperty appWebImageProperty) {
        UserUtil.appWebImageProperty = appWebImageProperty;
    }

}
