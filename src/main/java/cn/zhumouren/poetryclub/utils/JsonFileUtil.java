package cn.zhumouren.poetryclub.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/6 8:29
 **/
public class JsonFileUtil {

    public static List<Map> getListMapByJsonFile(String path) throws IOException {
        File file = ResourceUtils.getFile(path);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, List.class);
    }

    public static List<Map> getListMapByJsonFile(File file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, List.class);
    }
}
