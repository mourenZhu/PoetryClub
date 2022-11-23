package cn.zhumouren.poetryclub.bean.entity;

import cn.zhumouren.poetryclub.constant.games.FfoVoteType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
public class FfoGameUserVoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserEntity userEntity;
    @Enumerated(EnumType.STRING)
    private FfoVoteType ffoVoteType;
    private LocalDateTime createTime;

    public FfoGameUserVoteEntity(UserEntity userEntity, FfoVoteType ffoVoteType, LocalDateTime createTime) {
        this.userEntity = userEntity;
        this.ffoVoteType = ffoVoteType;
        this.createTime = createTime;
    }
}
