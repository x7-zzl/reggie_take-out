package com.zzl.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonControllerTest {

    @Test
    void upload() {
        String fileName="abc.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println("文件名后缀:"+suffix);
    }
}