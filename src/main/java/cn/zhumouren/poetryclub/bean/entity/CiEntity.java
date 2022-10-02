package cn.zhumouren.poetryclub.bean.entity;

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

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/8/7 22:25
 **/

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class CiEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotEmpty
    private String brandName;

    @NotEmpty
    private String title;

    @Column(unique = true, length = 3072)
    @NotEmpty
    private String content;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CiEntity ciEntity = (CiEntity) o;
        return id != null && Objects.equals(id, ciEntity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
