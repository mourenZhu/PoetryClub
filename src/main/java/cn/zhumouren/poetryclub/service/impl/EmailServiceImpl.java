package cn.zhumouren.poetryclub.service.impl;

import cn.zhumouren.poetryclub.common.response.ResponseResult;
import cn.zhumouren.poetryclub.dao.UserVerificationCodeRedisDao;
import cn.zhumouren.poetryclub.service.EmailService;
import cn.zhumouren.poetryclub.util.VerificationCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final static String ADMIN_MAIL = "AiPoet@aipoet.cn";
    private final JavaMailSender mailSender;
    private final UserVerificationCodeRedisDao verificationCodeRedisDao;

    public EmailServiceImpl(JavaMailSender mailSender, UserVerificationCodeRedisDao verificationCodeRedisDao) {
        this.mailSender = mailSender;
        this.verificationCodeRedisDao = verificationCodeRedisDao;
    }

    @Override
    public ResponseResult<Boolean> sendVerificationCode(String mail) {
        String verificationCode = VerificationCodeUtil.generateVerificationCode();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true);
            //邮件发送人
            messageHelper.setFrom(ADMIN_MAIL);
            //邮件接收人,设置多个收件人地址
            InternetAddress[] internetAddressTo = InternetAddress.parse(mail);
            messageHelper.setTo(internetAddressTo);
            //邮件主题
            message.setSubject("aipoet.cn 验证码");
            //邮件内容，html格式
            messageHelper.setText("验证码为: " + verificationCode, true);
            //发送
            mailSender.send(message);
            verificationCodeRedisDao.saveUserVerificationCode(mail, verificationCode);
            return ResponseResult.success();
        } catch (Exception e) {
            log.error("发给 {} 的验证邮件发送失败, 原因 {}", mail, e.getMessage());
            return ResponseResult.failedWithMsg(e.getMessage());
        }
    }
}
