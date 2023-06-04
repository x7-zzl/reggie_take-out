package com.zzl.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前用户id
 * 作用范围是某一个线程范围之内，相当于每一个线程中级的副本
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }
}
