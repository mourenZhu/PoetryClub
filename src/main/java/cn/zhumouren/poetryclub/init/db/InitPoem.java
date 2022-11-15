package cn.zhumouren.poetryclub.init.db;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.dao.PoemEntityRepository;
import cn.zhumouren.poetryclub.init.IInitData;
import cn.zhumouren.poetryclub.init.db.utils.PoemUtil;
import cn.zhumouren.poetryclub.property.AppInitProperty;
import com.google.common.collect.Lists;
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

    private final AppInitProperty appInitProperty;
    private final PoemEntityRepository poemEntityRepository;
    private final PoemUtil poemUtil;
    private final AsyncInitPoemServer asyncInitPoemServer;

    @Autowired
    public InitPoem(AppInitProperty appInitProperty, PoemEntityRepository poemEntityRepository, PoemUtil poemUtil, AsyncInitPoemServer asyncInitPoemServer) {
        this.appInitProperty = appInitProperty;
        this.poemEntityRepository = poemEntityRepository;
        this.poemUtil = poemUtil;
        this.asyncInitPoemServer = asyncInitPoemServer;
    }

    @Override
    public void init() {
        log.info("开始初始化诗");
        List<File> poemFileList = getPoemFileList();
        List<List<File>> partition = Lists.partition(poemFileList, 10);
        for (List<File> files : partition) {
            asyncInitPoemServer.InitPoemList(files);
        }
        log.info("等待异步......");
    }

    private void syncInitPoem() {

        List<File> poemFileList = getPoemFileList();
        final int[] contentMaxLen = {0};
        log.info("开始存入数据库");
        final int[] poemFileIndex = {0};
        poemFileList.forEach(file -> {
            List<PoemEntity> poemEntityList = new ArrayList<>();
            List<Map> poemMaps = null;
            try {
                poemMaps = poemUtil.getPoemMaps(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (Map map : poemMaps) {
                PoemEntity poemEntity = poemUtil.getPoemByMap(map);
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
        File files = new File(appInitProperty.getPoemFilesPath());
        return Arrays.stream(Objects.requireNonNull(files.listFiles()))
                .filter(file -> file.getName().matches("poet.*")).collect(Collectors.toList());
    }


}
