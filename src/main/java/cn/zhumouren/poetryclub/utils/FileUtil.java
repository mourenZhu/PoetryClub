package cn.zhumouren.poetryclub.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtil {

    public static String saveFileGetFileName(MultipartFile file, String path, String name) {
        saveFile(file, path, name);
        return name + getFileSuffixName(file);
    }

    /**
     * @param multipartFile 文件
     * @param path          文件要存入的文件夹路径
     * @param name          文件名（不带后缀）
     */
    public static void saveFile(MultipartFile multipartFile, String path, String name) {
        String suffix = getFileSuffixName(multipartFile);
        String realPath = path + name + suffix;
        //        logger.info("文件路径为:  " + realPath);
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(realPath);
            file.createNewFile();
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFileSuffixName(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

}
