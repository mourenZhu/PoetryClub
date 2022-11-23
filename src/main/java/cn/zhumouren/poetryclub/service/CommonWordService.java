package cn.zhumouren.poetryclub.service;

import cn.zhumouren.poetryclub.bean.entity.CommonWordEntity;

import java.util.List;

public interface CommonWordService {

    /**
     * 随机获取一个常用字
     *
     * @return
     */
    CommonWordEntity getCommonWordRandom();

    /**
     * 获取指定数量的常用字
     * @param top
     * @return
     */
    List<CommonWordEntity> topCommonWords(int top);


}
