package cn.zhumouren.poetryclub.init.db;

import cn.zhumouren.poetryclub.bean.entity.CommonWordEntity;
import cn.zhumouren.poetryclub.dao.CommonWordRepository;
import cn.zhumouren.poetryclub.init.IInitData;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Order(4)
@Component("initWordRanking")
public class InitCommonWord implements IInitData {

    private final CommonWordRepository commonWordRepository;
    private final InitPoem initPoem;
    private final AsyncInitCommonWordService asyncInitCommonWordService;
    private Map<Character, Integer> wordRankingMap;

    public InitCommonWord(CommonWordRepository commonWordRepository, InitPoem initPoem, AsyncInitCommonWordService asyncInitCommonWordService) {
        this.commonWordRepository = commonWordRepository;
        this.initPoem = initPoem;
        this.asyncInitCommonWordService = asyncInitCommonWordService;
        wordRankingMap = new ConcurrentHashMap<>();
    }

    @Override
    public void init() {
        log.info("开始初始化 字 使用排行");
        List<File> poemFileList = initPoem.getPoemFileList();
        List<List<File>> partition = Lists.partition(poemFileList, 10);
        List<CompletableFuture<Map<Character, Integer>>> completableFutures = new ArrayList<>();
        for (List<File> files : partition) {
            CompletableFuture<Map<Character, Integer>> future = asyncInitCommonWordService.getWordRankingMap(files);
            completableFutures.add(future);
        }
        log.info("开始等待 futures");
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        completableFutures.forEach(future -> mergeWordRankingMap(future.join()));
        save();
    }

    private void mergeWordRankingMap(Map<Character, Integer> map) {
        map.forEach((key, val) -> {
            if (wordRankingMap.containsKey(key)) {
                wordRankingMap.put(key, wordRankingMap.get(key) + val);
            } else {
                wordRankingMap.put(key, 1);
            }
        });
    }

    private void save() {
        log.info("共有字 {} 个", wordRankingMap.size());
        log.info("开始把使用字排行存入数据库");
        wordRankingMap.forEach((key, val) -> {
            if (!commonWordRepository.existsByWord(key)) {
                commonWordRepository.save(new CommonWordEntity(key, val));
            }
        });
        log.info("字 排行 已初始化完成");
    }


}
