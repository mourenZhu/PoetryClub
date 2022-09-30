package cn.zhumouren.poetryclub.common.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/20 8:24
 **/
public class ResponseUtil {

    public static void writeJsonByString(HttpServletResponse response, String s) {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try {
            response.getWriter().write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
