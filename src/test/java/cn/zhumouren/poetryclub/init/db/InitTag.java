package cn.zhumouren.poetryclub.init.db;

import cn.zhumouren.poetryclub.bean.entity.LiteratureTagEntity;
import cn.zhumouren.poetryclub.dao.LiteratureTagEntityRepository;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
@SpringBootTest
public class InitTag {

    @Autowired
    private LiteratureTagEntityRepository tagEntityRepository;

    @Test
    public void initTag() throws IOException {
        List<Map> poemMaps = InitPoem.getPoemMaps();
        Set<String> strTagSet = new HashSet<>();
        poemMaps.forEach(poemMap -> {
            List<String> tags = (List<String>) poemMap.get("tags");
            if (tags != null) {
                tags.forEach(tag -> {
                    strTagSet.add(ZhConverterUtil.toSimple(tag));
                });
            }
        });
        System.out.println("标签共有: " + strTagSet.size() + "个。");

        Set<LiteratureTagEntity> tagEntitySet = new HashSet<>();
        strTagSet.forEach(tag -> {
            tagEntitySet.add(new LiteratureTagEntity(tag));
        });

        System.out.println("开始保存tags");
        tagEntityRepository.saveAll(tagEntitySet);
        System.out.println("结束");
    }
}
