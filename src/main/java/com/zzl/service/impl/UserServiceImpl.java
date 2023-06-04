package com.zzl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.entity.User;
import com.zzl.mapper.UserMapper;
import com.zzl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    //抑制报错
    @SuppressWarnings("all")
    @Autowired
    private JavaMailSender javaMailSender;

    //发送人
    @Value("${spring.mail.username}")
    private String from;

    private String title="用户您好，您的验证码是:";

    //发送邮件
    //接收人toMail
    @PostMapping("/sendMsg")
    @Override
    public void sendMail(String toMail,String code) {
        try {

            MimeMessage message=javaMailSender.createMimeMessage();
            //允许添加附件
            MimeMessageHelper helper = new MimeMessageHelper(message,true);

            helper.setFrom(from+"(瑞吉外卖)");

            helper.setTo(toMail);
            helper.setSubject(title);

            //打开才能使用超链接
            helper.setText("<h1>"+code+"</h1>",true);


         /*   //添加附件
            File file = new File("D:\\IntelliJ IDEA 2021.1.2\\SpringBoot\\设计模式\\src\\设计模式分类.txt");
            helper.addAttachment("设计模式",file);*/

            javaMailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

}
