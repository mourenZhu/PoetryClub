package cn.zhumouren.poetryclub.bean.entity;

import cn.zhumouren.poetryclub.core.ILiterature;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * @author mourenZhu
 * @version 1.0
 * @description 诗实体
 * @date 2022/8/1 10:52
 **/
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class PoemEntity implements ILiterature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private AuthorEntity author;

    @NotEmpty
    private String title;

    @Column(unique = true, length = 3072)
    @NotEmpty
    private String content;

    @ManyToMany
    @ToString.Exclude
    private Set<LiteratureTagEntity> tags;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;


    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PoemEntity that = (PoemEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
