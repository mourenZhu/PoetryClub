package cn.zhumouren.poetryclub.bean.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class WordRankingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private Character word;

    @Min(0)
    private Integer usageCount;

    public WordRankingEntity(char word, int usageCount) {
        this.word = word;
        this.usageCount = usageCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordRankingEntity that = (WordRankingEntity) o;
        return getWord().equals(that.getWord());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWord());
    }
}
