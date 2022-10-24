package cn.zhumouren.poetryclub.init.db.utils;

import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import cn.zhumouren.poetryclub.bean.entity.LiteratureTagEntity;
import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.core.PoemType;
import cn.zhumouren.poetryclub.dao.AuthorEntityRepository;
import cn.zhumouren.poetryclub.util.JsonFileUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 11:42
 **/
@Component
public class PoemUtil {

    private final AuthorEntityRepository authorEntityRepository;
    private final Map<String, AuthorEntity> authorMap = new HashMap<>();
    private final Map<String, LiteratureTagEntity> tagMap = new HashMap<>();

    public PoemUtil(AuthorEntityRepository authorEntityRepository) {
        this.authorEntityRepository = authorEntityRepository;
    }

    public List<Map> getPoemMaps(File file) throws IOException {
        List<Map> poemMaps = JsonFileUtil.getListMapByJsonFile(file);
        String fileName = file.getName();
        if (fileName.matches(".*tang.*")) {
            addMapsEar(poemMaps, "唐");
        } else if (fileName.matches(".*song.*")) {
            addMapsEar(poemMaps, "宋");
        }
//        log.info("共有 {} 首诗", poemMaps.size());
        return poemMaps;
    }

    private void addMapsEar(List<Map> maps, String ear) {
        for (Map map : maps) {
            map.put("ear", ear);
        }
    }

    public PoemEntity getPoemByMap(Map map) {
        PoemEntity poemEntity = new PoemEntity();
        // 设置标题
        poemEntity.setTitle(ZhConverterUtil.toSimple((String) map.get("title")));
        // 设置正文
        String content = ZhConverterUtil.toSimple(getPoemContentByList((List<String>) map.get("paragraphs")));
        poemEntity.setContent(content);
        // 设置作者
        String author = ZhConverterUtil.toSimple((String) map.get("author"));
        AuthorEntity authorEntity = authorMap.get(author);
        // 如果作者不在数据库中，则创建作者并存入数据库
        if (authorEntity == null) {
            authorEntity = new AuthorEntity();
            authorEntity.setEra((String) map.get("era"));
            authorEntity.setName(author);
            authorEntity = authorEntityRepository.save(authorEntity);
            if (ObjectUtils.isNotEmpty(authorEntity.getEra())) {
                authorMap.put(author, authorEntity);
            }
        }

        // 设置诗文类型
        poemEntity.setPoemType(getPoemTypeByParagraphs((List<String>) map.get("paragraphs")));

        // 设置tags
        Set<LiteratureTagEntity> literatureTagEntitySet = new HashSet<>();
        List<String> tags = (List<String>) map.get("tags");
        if (tags == null) {
            tags = new ArrayList<>();
        }
        for (String tag : tags) {
            literatureTagEntitySet.add(tagMap.get(tag));
        }
        poemEntity.setTags(literatureTagEntitySet);

        return poemEntity;
    }

    private String getPoemContentByList(List<String> strings) {
        StringBuilder content = new StringBuilder();
        for (String s : strings) {
            content.append(s);
        }
        return content.toString();
    }

    public PoemType getPoemTypeByParagraphs(List<String> strings) {
        int rows = strings.size();
        int firstRowLen = strings.get(0).length();
        if (rows == 2) {
            if (firstRowLen == 12) {
                return PoemType.FIVE_CHARACTER_QUATRAIN;
            }
            if (firstRowLen == 14) {
                return PoemType.SIX_CHARACTER_QUATRAIN;
            }
            if (firstRowLen == 16) {
                return PoemType.SEVEN_CHARACTER_QUATRAIN;
            }
        } else if (rows == 4) {
            if (firstRowLen == 12) {
                return PoemType.FIVE_CHARACTER_RHYTHM_POEM;
            }
            if (firstRowLen == 14) {
                return PoemType.SIX_CHARACTER_RHYTHM_POEM;
            }
            if (firstRowLen == 16) {
                return PoemType.SEVEN_CHARACTER_RHYTHM_POEM;
            }
        } else if (rows > 4) {
            if (firstRowLen == 12) {
                return PoemType.FIVE_CHARACTER_LONG_RHYTHM_POEM;
            }
            if (firstRowLen == 14) {
                return PoemType.SIX_CHARACTER_LONG_RHYTHM_POEM;
            }
            if (firstRowLen == 16) {
                return PoemType.SEVEN_CHARACTER_LONG_RHYTHM_POEM;
            }
        }
        return PoemType.UNKNOWN;
    }
}
