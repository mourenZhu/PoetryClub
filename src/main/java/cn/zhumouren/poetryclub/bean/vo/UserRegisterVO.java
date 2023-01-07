package cn.zhumouren.poetryclub.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for the {@link cn.zhumouren.poetryclub.bean.entity.UserEntity} entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterVO implements Serializable {
    @Length(message = "昵称长度为 1-20", min = 1, max = 20)
    private String nickname;
    @Length(message = "用户名长度为6-30", min = 6, max = 30)
    private String username;
    @Length(message = "密码长度必须为6-30", min = 6, max = 30)
    private String password;
    @Email(message = "请输入正确的邮箱")
    private String email;
    @NotNull
    private String verificationCode;
}