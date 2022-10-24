package cn.zhumouren.poetryclub.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
public class FileUtil {

    public static String saveFileGetFileName(MultipartFile file, String path, String name) {
        saveFile(file, path, name);
        return name + getFileSuffixName(file);
    }

    /**
     * 保存文件，如果文件存在，则删除后再创建
     *
     * @param multipartFile 文件
     * @param path          文件要存入的文件夹路径
     * @param name          文件名（不带后缀）
     */
    public static void saveFile(MultipartFile multipartFile, String path, String name) {
        log.debug("file name = {}, originalFilename = {}, contentType = {}, size = {}",
                multipartFile.getName(), multipartFile.getOriginalFilename(),
                multipartFile.getContentType(), multipartFile.getSize());
        String suffix = getFileSuffixName(multipartFile);
        String realPath = path + "\\" + name + suffix;
        log.debug("文件路径为:  " + realPath);
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(realPath);
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    private static String getFileSuffixName(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

}
