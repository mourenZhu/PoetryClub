package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import java.io.Serializable;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.UserEntity} entity
 */
@Data
public class UserReqVO implements Serializable {
    @Length(max = 20)
    private String nickname;
    @Email
    private String email;
}