package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.dao.PoemEntityRepository;
import cn.zhumouren.poetryclub.service.PoemEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoemEntityServiceImpl implements PoemEntityService {

    private final PoemEntityRepository poemEntityRepository;

    public PoemEntityServiceImpl(PoemEntityRepository poemEntityRepository) {
        this.poemEntityRepository = poemEntityRepository;
    }

    @Override
    public List<PoemEntity> listBySentenceWordIndex(Character word, int index) {
        String regex = String.format("(^[\\u4e00-\\u9fa5]{%d}%c.*|^.*?[，。？]+[\\u4e00-\\u9fa5]{%d}%c.*)", index, word, index, word);
        return poemEntityRepository.findByRegex(regex);
    }
}
