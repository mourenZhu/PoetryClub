package cn.zhumouren.poetryclub.bean.entity;

import cn.zhumouren.poetryclub.core.ILiterature;
import cn.zhumouren.poetryclub.core.PoemType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
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
public class PoemEntity implements ILiterature, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AuthorEntity author;

    // CREATE INDEX idx_gin_poem_entity_title ON poem_entity USING gin (to_tsvector('chinese_zh', title));
    @NotEmpty
    private String title;

    // CREATE INDEX idx_gin_poem_entity_content ON poem_entity USING gin (to_tsvector('chinese_zh', content));
    @Column(length = 3072, columnDefinition = "text")
    @NotEmpty
    private String content;

    @Enumerated(EnumType.STRING)
    private PoemType poemType;

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
