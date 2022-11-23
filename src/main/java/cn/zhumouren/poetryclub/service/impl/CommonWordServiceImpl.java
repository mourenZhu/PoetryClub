package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.entity.CommonWordEntity;
import cn.zhumouren.poetryclub.dao.CommonWordRepository;
import cn.zhumouren.poetryclub.service.CommonWordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonWordServiceImpl implements CommonWordService {

    private final CommonWordRepository commonWordRepository;

    public CommonWordServiceImpl(CommonWordRepository commonWordRepository) {
        this.commonWordRepository = commonWordRepository;
    }

    @Override
    public CommonWordEntity getCommonWordRandom() {
        int index = (int) (Math.random() * 200);
        return commonWordRepository.findCommonlyUsedWord(index);
    }

    @Override
    public List<CommonWordEntity> topCommonWords(int top) {
        return commonWordRepository.findTopCommonWord(top);
    }
}
