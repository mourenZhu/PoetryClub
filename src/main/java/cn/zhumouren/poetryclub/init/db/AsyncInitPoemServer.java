package cn.zhumouren.poetryclub.init.db;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.dao.PoemEntityRepository;
import cn.zhumouren.poetryclub.init.db.utils.PoemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 10:49
 **/
@Service
@Slf4j
public class AsyncInitPoemServer {

    private final PoemEntityRepository poemEntityRepository;
    private final PoemUtil poemUtil;


    public AsyncInitPoemServer(PoemEntityRepository poemEntityRepository, PoemUtil poemUtil) {
        this.poemEntityRepository = poemEntityRepository;
        this.poemUtil = poemUtil;
    }

    @Async("pgsqlExecutor")
    public CompletableFuture<Boolean> InitPoemList(List<File> poemFileList) {
        Thread t = Thread.currentThread();
        int currentIndex = 0;
        for (File poemFile : poemFileList) {
            List<PoemEntity> poemEntityList = new ArrayList<>();
            List<Map> poemMaps = null;
            try {
                poemMaps = poemUtil.getPoemMaps(poemFile);
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
            }
            currentIndex++;
            log.info("线程: {}, 共有 {} 个文件， 还有 {} 个文件需要初始化，文件: {}, 共有 {} 首诗",
                    t.getName(), poemFileList.size(), poemFileList.size() - currentIndex,
                    poemFile.getName(), poemEntityList.size());
            poemEntityRepository.saveAll(poemEntityList);
        }
        log.info("线程: {} 结束。", t.getName());
        return CompletableFuture.completedFuture(true);
    }


}
