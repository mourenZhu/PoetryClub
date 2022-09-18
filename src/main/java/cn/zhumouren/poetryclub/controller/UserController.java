package cn.zhumouren.poetryclub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/14 18:13
 **/
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
