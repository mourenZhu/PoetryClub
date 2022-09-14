package cn.zhumouren.poetryclub.init.db;

import cn.zhumouren.poetryclub.bean.entity.LiteratureTagEntity;
import cn.zhumouren.poetryclub.dao.LiteratureTagEntityRepository;
import cn.zhumouren.poetryclub.init.IInitData;
import cn.zhumouren.poetryclub.init.db.utils.PoemUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/6 9:23
 **/
@Component
@Order(1)
@Slf4j
public class InitTag implements IInitData {

    private final LiteratureTagEntityRepository tagEntityRepository;
    private final InitPoem initPoem;
    private final PoemUtil poemUtil;

    public InitTag(LiteratureTagEntityRepository tagEntityRepository, InitPoem initPoem, PoemUtil poemUtil) {

        this.tagEntityRepository = tagEntityRepository;
        this.initPoem = initPoem;
        this.poemUtil = poemUtil;
    }

    @Override
    public void init() {

        Set<String> strTagSet = new HashSet<>();

        initPoem.getPoemFileList().forEach(file -> {
            try {
                List<Map> poemMaps = poemUtil.getPoemMaps(file);
                poemMaps.forEach(poemMap -> {
                    List<String> tags = (List<String>) poemMap.get("tags");
                    if (tags != null) {
                        tags.forEach(tag -> {
                            strTagSet.add(ZhConverterUtil.toSimple(tag));
                        });
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("标签共有: {} 个。", strTagSet.size());

        Set<LiteratureTagEntity> tagEntitySet = new HashSet<>();
        strTagSet.forEach(tag -> {
            tagEntitySet.add(new LiteratureTagEntity(tag));
        });

        log.info("开始保存tags");
        tagEntityRepository.saveAll(tagEntitySet);
        log.info("结束");
    }
}
