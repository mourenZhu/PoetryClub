package cn.zhumouren.poetryclub.bean.vo;

import cn.zhumouren.poetryclub.constant.games.FfoVoteType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.FfoGameUserVoteEntity} entity
 */
@Data
public class FfoGameUserVoteResDto implements Serializable {
    private final UserPublicResVo userEntity;
    private final FfoVoteType ffoVoteType;
    private final LocalDateTime createTime;
}