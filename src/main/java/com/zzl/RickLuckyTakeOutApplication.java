package com.zzl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
//加此注解才会扫描过滤器
@ServletComponentScan
//开启事务注解
@EnableTransactionManagement
public class RickLuckyTakeOutApplication {

    public static void main(String[] args) {

        SpringApplication.run(RickLuckyTakeOutApplication.class, args);
        log.info("项目启动成功");
    }

}
