package cn.zhumouren.poetryclub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/17 19:31
 **/
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
