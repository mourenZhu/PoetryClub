package cn.zhumouren.poetryclub.bean.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
public class FfoGameUserInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity user;


    @Range(min = 1, max = 10)
    private Integer sequence;

    @Range(min = 1, max = 10)
    private Integer ranking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FfoGameUserInfoEntity that = (FfoGameUserInfoEntity) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
