package cn.zhumouren.poetryclub.controller;

import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("verification_code/{email}")
    public ResponseResult<Boolean> applyVerificationCode(@PathVariable String email) {
        log.debug("mail = {}", email);
        return emailService.sendVerificationCode(email);
    }
}
