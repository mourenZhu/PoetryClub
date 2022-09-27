package cn.zhumouren.poetryclub.common.response;

import lombok.Data;

@Data
public class LoginRes {
    private String token;
    public LoginRes(String token) {
        this.token = token;
    }

    public static LoginRes createLoginRes(String token) {
        return new LoginRes(token);
    }
}
