package cn.zhumouren.poetryclub.bean.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import java.util.Objects;

@NoArgsConstructor
@Data
@Entity
public class FfoGameUserInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserEntity userEntity;

    @Range(min = 1, max = 10)
    private Integer sequence;
    @Range(min = 1, max = 10)
    private Integer ranking;
    public FfoGameUserInfoEntity(UserEntity userEntity, int sequence, int ranking) {
        this.userEntity = userEntity;
        this.sequence = sequence;
        this.ranking = ranking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (id == null) return true;
        FfoGameUserInfoEntity that = (FfoGameUserInfoEntity) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
