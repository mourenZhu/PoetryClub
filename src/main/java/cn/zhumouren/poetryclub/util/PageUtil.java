package cn.zhumouren.poetryclub.util;


import cn.zhumouren.poetryclub.config.PageConfig;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/5/1 18:53
 **/
public class PageUtil {
    public static Pageable getPageable(Integer pageNum, Integer pageSize) {

        if (pageNum == null || pageNum < 0) {
            pageNum = PageConfig.getDefaultPageNum();
        }

        if (pageSize == null || pageSize < 0 || pageSize > PageConfig.getMaxPageSize()) {
            pageSize = PageConfig.getDefaultPageSize();
        }
        return PageRequest.of(pageNum, pageSize);
    }

    public static Pageable getPageable() {
        return PageRequest.of(PageConfig.getDefaultPageNum(), PageConfig.getDefaultPageSize());
    }
}
