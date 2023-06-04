package com.zzl.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zzl.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 自定义元数据对象处理器
 *
 */
@Slf4j
@Component
//公共字段处理器
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入字段自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject ) {
        log.info("公共字段填充[insert]");
        log.info("插入->"+metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());


        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }

    /**
     * 更新修改时自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]");
        log.info("修改->"+metaObject.toString());

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
