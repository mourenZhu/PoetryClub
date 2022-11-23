package cn.zhumouren.poetryclub.init.db;

import cn.zhumouren.poetryclub.init.db.utils.PoemUtil;
import cn.zhumouren.poetryclub.util.JsonFileUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AsyncInitCommonWordService {

    @Async("initExecutor")
    public CompletableFuture<Map<Character, Integer>> getWordRankingMap(List<File> poemFileList) {
        Thread t = Thread.currentThread();
        log.info("线程: {}， 开始从文件中获取字使用量", t.getName());
        Map<Character, Integer> wordRankingMap = new HashMap<>();
        for (int i = 0; i < poemFileList.size(); i++) {
            File poemFile = poemFileList.get(i);
            List<Map> poemMaps;
            try {
                poemMaps = JsonFileUtil.getListMapByJsonFile(poemFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (Map poemMap : poemMaps) {
                String content = ZhConverterUtil.toSimple(PoemUtil.getPoemContentByList((List<String>) poemMap.get("paragraphs")));
                char[] chars = content.toCharArray();
                for (char c : chars) {
                    if (c == '？' || c == '，' || c == '。' || c == '；' || c == '?') {
                        continue;
                    }
                    if (wordRankingMap.containsKey(c)) {
                        wordRankingMap.put(c, wordRankingMap.get(c) + 1);
                    } else {
                        wordRankingMap.put(c, 1);
                    }
                }
            }
            log.info("线程: {}，共有 {} 个文件，还有 {} 个文件需要提取文字", t.getName(), poemFileList.size(), poemFileList.size() - (i + 1));
        }
        log.info("线程: {} 结束，共有文字 {} 种", t.getName(), wordRankingMap.size());
        return CompletableFuture.supplyAsync(() -> wordRankingMap);
    }
}
