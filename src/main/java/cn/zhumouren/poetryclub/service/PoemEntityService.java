package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;

import java.util.List;

public interface PoemEntityService {

    /**
     * 通过字在每段句子中的位置在找诗（及飞花令的规则）
     *
     * @param word  飞花令的字
     * @param index 在哪个位置
     * @return
     */
    List<PoemEntity> listBySentenceWordIndex(Character word, int index);
}
