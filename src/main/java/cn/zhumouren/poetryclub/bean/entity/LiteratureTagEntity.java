package cn.zhumouren.poetryclub.bean.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author mourenZhu
 * @version 1.0
 * @description 作品标签
 * @date 2022/8/1 11:32
 **/
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class LiteratureTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, unique = true)
    @NotEmpty
    private String tag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        LiteratureTagEntity that = (LiteratureTagEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public LiteratureTagEntity(String tag) {
        this.tag = tag;
        this.createTime = LocalDateTime.now();
    }
}
