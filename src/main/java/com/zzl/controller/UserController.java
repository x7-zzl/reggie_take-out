package com.zzl.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzl.common.R;
import com.zzl.entity.User;
import com.zzl.service.UserService;
import com.zzl.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

//    @SuppressWarnings("all")
    @Autowired
    private RedisTemplate redisTemplate;//注入redis对象

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取用户的邮箱
        String phone=user.getPhone();

        if(Strings.isNotEmpty(phone)){
            //生成4位验证码
            String code= ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码:{}",code);
            //发送验证码
            userService.sendMail(phone,code);

            //验证码保存到session
//            session.setAttribute(phone,code);

            //将保存的验证码缓存到redis中，并且设置有效期为5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);


            return R.success("验证码发送成功");
        }

        return R.error("验证码发送失败");
    }


    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);

        //从redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if(codeInSession != null && codeInSession.equals(code)){
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            //如果用户登录成功，删除redis中缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登录失败");
    }
}
