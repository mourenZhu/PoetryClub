package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.dao.CommonWordRepository;
import cn.zhumouren.poetryclub.service.CommonWordService;
import org.springframework.stereotype.Service;

@Service
public class CommonWordServiceImpl implements CommonWordService {

    private final CommonWordRepository commonWordRepository;

    public CommonWordServiceImpl(CommonWordRepository commonWordRepository) {
        this.commonWordRepository = commonWordRepository;
    }

    @Override
    public char getCommonlyUsedWordRandom() {
        int index = (int) (Math.random() * 200);
        return commonWordRepository.findCommonlyUsedWord(index).getWord();
    }
}
