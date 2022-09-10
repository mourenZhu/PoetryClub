package cn.zhumouren.poetryclub.init.db;

import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import cn.zhumouren.poetryclub.bean.entity.LiteratureTagEntity;
import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.dao.AuthorEntityRepository;
import cn.zhumouren.poetryclub.dao.LiteratureTagEntityRepository;
import cn.zhumouren.poetryclub.dao.PoemEntityRepository;
import cn.zhumouren.poetryclub.init.IInitData;
import cn.zhumouren.poetryclub.utils.JsonFileUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/6 9:21
 **/
@Component
@Order(2)
@Log4j2
public class InitPoem implements IInitData {

    private static final String POEM_FILES_PATH = "C:\\data\\github-project\\chinese-poetry\\json";

    private final Map<String, AuthorEntity> authorMap = new HashMap<>();
    private final Map<String, LiteratureTagEntity> tagMap = new HashMap<>();

    private final PoemEntityRepository poemEntityRepository;
    private final AuthorEntityRepository authorEntityRepository;
    private final LiteratureTagEntityRepository literatureTagEntityRepository;


    @Autowired
    public InitPoem(PoemEntityRepository poemEntityRepository,
                    AuthorEntityRepository authorEntityRepository,
                    LiteratureTagEntityRepository literatureTagEntityRepository) {
        this.poemEntityRepository = poemEntityRepository;
        this.authorEntityRepository = authorEntityRepository;
        this.literatureTagEntityRepository = literatureTagEntityRepository;
        this.authorEntityRepository.findAll().forEach(authorEntity
                -> authorMap.put(authorEntity.getName(), authorEntity));
        this.literatureTagEntityRepository.findAll().forEach(literatureTagEntity
                -> tagMap.put(literatureTagEntity.getTag(), literatureTagEntity));
    }

    public static List<Map> getPoemMaps() throws IOException {
        List<Map> poemMaps = new ArrayList<>();
        File files = new File(POEM_FILES_PATH);
        File[] listFiles = files.listFiles();
        List<File> poemFiles = new ArrayList<>();
        // 把文件夹中的诗文件加入 poemFiles 中
        assert listFiles != null;
        Arrays.stream(listFiles).forEach(file -> {
            //            System.out.println("name = " + file.getName() + "   path = " + file.getPath());
            if (file.getName().matches("poet.*")) {
                poemFiles.add(file);
            }
        });

        // 把文件中的诗存入list map 中
        for (File file : poemFiles) {
            List<Map> maps = JsonFileUtil.getListMapByJsonFile(file);
            String fileName = file.getName();
            if (fileName.matches(".*tang.*")) {
                addMapsEar(maps, "唐");
            } else if (fileName.matches(".*song.*")) {
                addMapsEar(maps, "宋");
            }
            poemMaps.addAll(maps);
        }
        log.info("共有 {} 首诗", poemMaps.size());
        return poemMaps;
    }

    private static void addMapsEar(List<Map> maps, String ear) {
        for (Map map : maps) {
            map.put("ear", ear);
        }
    }

    @Override
    public void init() {
        log.info("开始初始化诗");
        List<Map> poemMaps = null;
        try {
            poemMaps = getPoemMaps();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<PoemEntity> poemEntityList = new ArrayList<>();
        int contentMaxLen = 0;
        for (Map map : poemMaps) {
            PoemEntity poemEntity = getPoemByMap(map);
            if (ObjectUtils.isNotEmpty(poemEntity.getContent())) {
                poemEntityList.add(poemEntity);
            }
            if (poemEntity.getContent().length() > contentMaxLen) {
                contentMaxLen = poemEntity.getContent().length();
            }
        }
        log.info("最大诗词长度为: {}", contentMaxLen);
        log.info("开始存入数据库");
        poemEntityRepository.saveAll(poemEntityList);

    }

    private PoemEntity getPoemByMap(Map map) {
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
}
