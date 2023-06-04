package com.zzl.service.impl;

import com.zzl.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceImplTest {


    @Autowired
    public UserService userService;
    @Test
    void sendMail() {
        userService.sendMail("3490596334@qq.com","1234");
        System.out.println("发送成功");
    }
}