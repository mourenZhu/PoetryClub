package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.PoemEntity;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface PoemEntityService {

    /**
     * 通过字在每段句子中的位置在找诗（及飞花令的规则）
     *
     * @param word  飞花令的字
     * @param index 在哪个位置
     * @return
     */
    List<PoemEntity> listBySentenceWordIndex(Character word, int index);

    /**
     * 通过一个句子来查找诗，找到的条件是，句子必须相同
     *
     * @param sentence
     * @return
     */
    List<PoemEntity> listBySentence(String sentence);

    /**
     * 古诗的复杂查询
     *
     * @param author
     * @param title
     * @param content
     * @param tags
     * @param keyword
     * @param keywordIndex
     * @param pageable
     * @return
     */
    ResponseResult<Page<PoemEntity>> listPoem(
            String author, String title, String content, Set<String> tags,
            Character keyword, Integer keywordIndex, Pageable pageable);
}
