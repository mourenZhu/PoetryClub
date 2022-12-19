package cn.zhumouren.poetryclub.bean.entity;

import cn.zhumouren.poetryclub.bean.vo.AnnouncementPostVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@Data
@Entity
public class AnnouncementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public AnnouncementEntity(AnnouncementPostVo announcementPostVo) {
        this.title = announcementPostVo.getTitle();
        this.content = announcementPostVo.getContent();
        this.createTime = LocalDateTime.now();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnouncementEntity that = (AnnouncementEntity) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
