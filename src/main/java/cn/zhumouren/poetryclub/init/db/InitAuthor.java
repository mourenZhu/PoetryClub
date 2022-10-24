package cn.zhumouren.poetryclub.init.db;

import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import cn.zhumouren.poetryclub.dao.AuthorEntityRepository;
import cn.zhumouren.poetryclub.init.IInitData;
import cn.zhumouren.poetryclub.properties.AppInitProperties;
import cn.zhumouren.poetryclub.util.JsonFileUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/5 19:08
 **/
@Component
@Order(1)
@Slf4j
public class InitAuthor implements IInitData {

    private static final String SONG_AUTHORS_FILE_PATH = "classpath:authors/authors.song.json";
    private static final String SONG_CI_AUTHORS_FILE_PATH = "classpath:authors/ci_author.song.json";
    private static final String TANG_AUTHORS_FILE_PATH = "classpath:authors/authors.tang.json";
    private final AppInitProperties appInitProperties;

    private final AuthorEntityRepository authorEntityRepository;

    public InitAuthor(AppInitProperties appInitProperties, AuthorEntityRepository authorEntityRepository) {
        this.appInitProperties = appInitProperties;
        this.authorEntityRepository = authorEntityRepository;
    }

    @Override
    public void init() {
        log.info("开始初始化作者");
        List<File> authorsFileList = getAuthorsFileList();
        authorsFileList.forEach(file -> {
            String era = "";
            if (file.getName().matches(".*song.*")) {
                era = "宋";
            } else if (file.getName().matches(".*tang.*")) {
                era = "唐";
            }
            try {
                savaAuthor(file, era);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<File> getAuthorsFileList() {
        File files = new File(appInitProperties.getPoemFilesPath());
        return Arrays.stream(Objects.requireNonNull(files.listFiles()))
                .filter(file -> file.getName().matches("authors.*")).collect(Collectors.toList());
    }

    private void savaAuthor(File file, String era) throws IOException {
        List<Map> tangAuthorMapList = JsonFileUtil.getListMapByJsonFile(file);
        log.info("{} 朝诗人共有 {} 人", era, tangAuthorMapList.size());
        List<AuthorEntity> authorEntityList = new ArrayList<>();
        tangAuthorMapList.forEach(authorMap -> {
            AuthorEntity author = getAuthorEntityByMap(authorMap);
            author.setEra(era);
            authorEntityList.add(author);
        });
        log.info("开始存入数据库");
        authorEntityRepository.saveAll(authorEntityList);
        log.info("{} 结束", era);
    }

    private AuthorEntity getAuthorEntityByMap(Map map) {
        AuthorEntity authorEntity = new AuthorEntity();
        authorEntity.setName(ZhConverterUtil.toSimple((String) map.get("name")));
        authorEntity.setDescription(ZhConverterUtil.toSimple((String) map.get("desc")));
        return authorEntity;
    }
}
