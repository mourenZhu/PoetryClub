package cn.zhumouren.poetryclub.core;

/**
 * @author mourenZhu
 * @version 1.0
 * @description 文学作品接口
 * @date 2022/8/1 10:40
 **/
public interface ILiterature {

    String getTitle();

    void setTitle(String title);

    String getContent();

    void setContent(String content);

}
