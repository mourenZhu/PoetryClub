package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.dao.PoemRepository;
import cn.zhumouren.poetryclub.service.PoemEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoemEntityServiceImpl implements PoemEntityService {

    private final PoemRepository poemRepository;

    public PoemEntityServiceImpl(PoemRepository poemRepository) {
        this.poemRepository = poemRepository;
    }

    @Override
    public List<PoemEntity> listBySentenceWordIndex(Character word, int index) {
        String regex = String.format("(^[\\u4e00-\\u9fa5]{%d}%c.*|^.*?[，。？；]+[\\u4e00-\\u9fa5]{%d}%c.*)", index, word, index, word);
        return poemRepository.findByRegex(regex);
    }

    @Override
    public List<PoemEntity> listBySentence(String sentence) {
        String regex = String.format("(^%s?[，。？；].*|^.*?[，。？]%s?[，。？；].*)", sentence, sentence);
        return poemRepository.findByRegex(regex);
    }
}
