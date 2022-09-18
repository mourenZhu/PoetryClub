package cn.zhumouren.poetryclub.bean.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/18 16:03
 **/
@Data
public class LoginVO implements Serializable {
    @Length(message = "用户名不能为空", min = 6, max = 30)
    private String username;
    @NotEmpty
    private String password;
}
