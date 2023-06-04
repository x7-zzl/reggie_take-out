package com.zzl.controller;

import com.zzl.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.UUID;

//进行文件上传下载的处理
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file 文件名不能乱起，要与前端上传时的name保持一致
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        //使用原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取文件后缀.jpg/.png
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用uuid重新生成文件名，防止造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录文件，
        File dir = new File(basePath);

        //文件不存在，创建文件
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        //通过输入流，读取文件
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流，通过response响应浏览器，写回浏览器,在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            //图片文件
            response.setContentType("image/jpeg");
            int len=0;
            byte[] bytes=new byte[1024];

            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
