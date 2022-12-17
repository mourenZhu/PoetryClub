package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class AdminChangePasswordVo {
    @Length(message = "密码长度必须为6-30", min = 6, max = 30)
    String newPassword;
}
