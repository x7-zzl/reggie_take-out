package com.zzl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.entity.User;

public interface UserService extends IService<User> {
    //发送邮箱
    public void sendMail(String toMail,String code);
}
