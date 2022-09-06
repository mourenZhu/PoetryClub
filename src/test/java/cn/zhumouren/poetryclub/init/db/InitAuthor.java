package cn.zhumouren.poetryclub.init.db;

import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import cn.zhumouren.poetryclub.dao.AuthorEntityRepository;
import cn.zhumouren.poetryclub.utils.JsonFileUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
@SpringBootTest
public class InitAuthor {

    private static final String TANG_AUTHORS_FILE_PATH = "classpath:authors/authors.tang.json";

    private static final String SONG_AUTHORS_FILE_PATH = "classpath:authors/authors.song.json";

    private static final String SONG_CI_AUTHORS_FILE_PATH = "classpath:authors/ci_author.song.json";

    @Autowired
    private AuthorEntityRepository authorEntityRepository;

    @Test
    public void initAuthor() throws IOException {
        System.out.println("开始");
        savaAuthor(TANG_AUTHORS_FILE_PATH, "唐");
        savaAuthor(SONG_AUTHORS_FILE_PATH, "宋");
        savaAuthor(SONG_CI_AUTHORS_FILE_PATH, "宋");
    }

    private void savaAuthor(String path, String era) throws IOException {
        List<Map> tangAuthorMapList = JsonFileUtil.getListMapByJsonFile(path);
        System.out.println(era + "朝诗人共有 " + tangAuthorMapList.size() + " 人");
        List<AuthorEntity> authorEntityList = new ArrayList<>();
        tangAuthorMapList.forEach(authorMap -> {
            AuthorEntity author = getAuthorEntityByMap(authorMap);
            author.setEra(era);
            authorEntityList.add(author);
        });
        System.out.println(era + " 开始存入数据库");
        authorEntityRepository.saveAll(authorEntityList);
        System.out.println(era + " 结束");
    }

    private AuthorEntity getAuthorEntityByMap(Map map) {
        AuthorEntity authorEntity = new AuthorEntity();
        authorEntity.setName(ZhConverterUtil.toSimple((String) map.get("name")));
        authorEntity.setDescription(ZhConverterUtil.toSimple((String) map.get("desc")));
        return authorEntity;
    }
}
