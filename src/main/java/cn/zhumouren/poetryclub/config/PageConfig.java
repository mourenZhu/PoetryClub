package cn.zhumouren.poetryclub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/5/1 19:02
 **/
@Component
@ConfigurationProperties("app.page-config")
public class PageConfig {
    private static Integer defaultPageNum;
    private static Integer defaultPageSize;
    private static Integer maxPageSize;

    public static Integer getDefaultPageNum() {
        return defaultPageNum;
    }

    public void setDefaultPageNum(Integer defaultPageNum) {
        PageConfig.defaultPageNum = defaultPageNum;
    }

    public static Integer getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(Integer defaultPageSize) {
        PageConfig.defaultPageSize = defaultPageSize;
    }

    public static Integer getMaxPageSize() {
        return maxPageSize;
    }

    public void setMaxPageSize(Integer maxPageSize) {
        PageConfig.maxPageSize = maxPageSize;
    }
}
