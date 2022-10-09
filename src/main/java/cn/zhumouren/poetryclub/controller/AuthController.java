package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.bean.entity.UserEntity;
import cn.zhumouren.poetryclub.bean.vo.LoginVO;
import cn.zhumouren.poetryclub.common.response.LoginRes;
import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.utils.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/8/30 17:34
 **/
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenUtil jwtUtil;


    public AuthController(AuthenticationManager authManager, JwtTokenUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseResult<LoginRes> login(@RequestBody @Validated LoginVO loginVO) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginVO.getUsername(), loginVO.getPassword())
            );
            UserEntity user = (UserEntity) authentication.getPrincipal();
            return ResponseResult.success(LoginRes.createLoginRes(jwtUtil.generateAccessToken(user)));
        } catch (BadCredentialsException ex) {
            return ResponseResult.forbidden("用户名或密码错误");
        }
    }

    /**
     * 暂时不做处理，一般处理流程事清空缓存
     *
     * @return
     */
    @PostMapping("/logout")
    public ResponseResult<Boolean> logout() {
        return ResponseResult.success();
    }
}
