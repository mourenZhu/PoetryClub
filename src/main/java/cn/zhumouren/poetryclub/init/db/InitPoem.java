package cn.zhumouren.poetryclub.init.db;

import cn.zhumouren.poetryclub.bean.entity.AuthorEntity;
import cn.zhumouren.poetryclub.bean.entity.LiteratureTagEntity;
import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.dao.AuthorEntityRepository;
import cn.zhumouren.poetryclub.dao.LiteratureTagEntityRepository;
import cn.zhumouren.poetryclub.dao.PoemEntityRepository;
import cn.zhumouren.poetryclub.init.IInitData;
import cn.zhumouren.poetryclub.properties.AppInitProperties;
import cn.zhumouren.poetryclub.utils.JsonFileUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @date 2022/9/6 9:21
 **/
@Component
@Order(2)
@Slf4j
public class InitPoem implements IInitData {

    private final AppInitProperties appInitProperties;
    private final Map<String, AuthorEntity> authorMap = new HashMap<>();
    private final Map<String, LiteratureTagEntity> tagMap = new HashMap<>();
    private final PoemEntityRepository poemEntityRepository;
    private final AuthorEntityRepository authorEntityRepository;
    private final LiteratureTagEntityRepository literatureTagEntityRepository;

    @Autowired
    public InitPoem(AppInitProperties appInitProperties, PoemEntityRepository poemEntityRepository,
                    AuthorEntityRepository authorEntityRepository,
                    LiteratureTagEntityRepository literatureTagEntityRepository) {
        this.appInitProperties = appInitProperties;
        this.poemEntityRepository = poemEntityRepository;
        this.authorEntityRepository = authorEntityRepository;
        this.literatureTagEntityRepository = literatureTagEntityRepository;
        this.authorEntityRepository.findAll().forEach(authorEntity
                -> authorMap.put(authorEntity.getName(), authorEntity));
        this.literatureTagEntityRepository.findAll().forEach(literatureTagEntity
                -> tagMap.put(literatureTagEntity.getTag(), literatureTagEntity));
    }

    @Override
    public void init() {
        log.info("开始初始化诗");
        List<File> poemFileList = getPoemFileList();
        final int[] contentMaxLen = {0};
        log.info("开始存入数据库");
        final int[] poemFileIndex = {0};
        poemFileList.forEach(file -> {
            List<PoemEntity> poemEntityList = new ArrayList<>();
            List<Map> poemMaps = null;
            try {
                poemMaps = getPoemMaps(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (Map map : poemMaps) {
                PoemEntity poemEntity = getPoemByMap(map);
                if (ObjectUtils.isEmpty(poemEntity.getTitle())) {
                    poemEntity.setTitle("无题");
                }
                if (ObjectUtils.isNotEmpty(poemEntity.getContent())) {
                    poemEntityList.add(poemEntity);
                }
                if (poemEntity.getContent().length() > contentMaxLen[0]) {
                    contentMaxLen[0] = poemEntity.getContent().length();
                }
            }
            log.info("文件: {}, 共有 {} 首诗, 还有 {} 个文件需要处理",
                    file.getName(), poemFileList.size(), poemFileList.size() - (++poemFileIndex[0]));
            poemEntityRepository.saveAll(poemEntityList);
        });
        log.info("最大诗词长度为: {}", contentMaxLen[0]);
    }

    public List<File> getPoemFileList() {
        File files = new File(appInitProperties.getPoemFilesPath());
        return Arrays.stream(Objects.requireNonNull(files.listFiles()))
                .filter(file -> file.getName().matches("poet.*")).collect(Collectors.toList());
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
