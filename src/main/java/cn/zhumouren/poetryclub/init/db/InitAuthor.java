package cn.zhumouren.poetryclub.init.db;

import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import cn.zhumouren.poetryclub.dao.AuthorEntityRepository;
import cn.zhumouren.poetryclub.init.IInitData;
import cn.zhumouren.poetryclub.utils.JsonFileUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/5 19:08
 **/
@Component
@Order(1)
@Log4j2
public class InitAuthor implements IInitData {

    private static final String TANG_AUTHORS_FILE_PATH = "classpath:authors/authors.tang.json";

    private static final String SONG_AUTHORS_FILE_PATH = "classpath:authors/authors.song.json";

    private static final String SONG_CI_AUTHORS_FILE_PATH = "classpath:authors/ci_author.song.json";

    private final AuthorEntityRepository authorEntityRepository;

    public InitAuthor(AuthorEntityRepository authorEntityRepository) {
        this.authorEntityRepository = authorEntityRepository;
    }

    @Override
    public void init() {
        log.info("开始初始化作者");
        try {
            savaAuthor(TANG_AUTHORS_FILE_PATH, "唐");
            savaAuthor(SONG_AUTHORS_FILE_PATH, "宋");
            savaAuthor(SONG_CI_AUTHORS_FILE_PATH, "宋");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void savaAuthor(String path, String era) throws IOException {
        List<Map> tangAuthorMapList = JsonFileUtil.getListMapByJsonFile(path);
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
