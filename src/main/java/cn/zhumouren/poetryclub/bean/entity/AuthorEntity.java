package cn.zhumouren.poetryclub.bean.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/8/1 10:44
 **/
@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    @NotEmpty
    private String name;

    @Column(length = 10)
    private String era;
    @Column(length = 2048)
    private String description;

    @Override
    public String toString() {
        return "AuthorEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", era='" + era + '\'' +
                '}';
    }

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AuthorEntity that = (AuthorEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @PostLoad
    public void postLoad() {

    }

}
