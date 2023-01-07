package cn.zhumouren.poetryclub.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@SpringBootTest
public class EmailServiceTest {
    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void sendSimpleMailTest() {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true);
            //邮件发送人
            messageHelper.setFrom("administrator@aipoet.cn");
            //邮件接收人,设置多个收件人地址
            InternetAddress[] internetAddressTo = InternetAddress.parse("zhumouren0623@qq.com");
            messageHelper.setTo(internetAddressTo);
            //messageHelper.setTo(to);
            //邮件主题
            message.setSubject("测试");
            //邮件内容，html格式
            messageHelper.setText("测试内容", true);
            //发送
            mailSender.send(message);
            System.out.println("发送成功");
        } catch (Exception e) {
            System.out.println("失败");
            System.out.println(e);
        }
    }
}
