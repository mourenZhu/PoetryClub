package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AnnouncementPutVo {
    @NotNull
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
}
