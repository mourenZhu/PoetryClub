package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.dao.WordRankingRepository;
import cn.zhumouren.poetryclub.service.WordRankingService;
import org.springframework.stereotype.Service;

@Service
public class WordRankingServiceImpl implements WordRankingService {

    private final WordRankingRepository wordRankingRepository;

    public WordRankingServiceImpl(WordRankingRepository wordRankingRepository) {
        this.wordRankingRepository = wordRankingRepository;
    }

    @Override
    public char getCommonlyUsedWordRandom() {
        int index = (int) (Math.random() * 200);
        return wordRankingRepository.findCommonlyUsedWord(index).getWord();
    }
}
