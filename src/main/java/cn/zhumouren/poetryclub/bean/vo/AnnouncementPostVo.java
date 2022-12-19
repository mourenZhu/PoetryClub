package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AnnouncementPostVo {

    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
}
