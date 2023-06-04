package com.zzl.filter;


import com.alibaba.fastjson.JSON;
import com.zzl.common.BaseContext;
import com.zzl.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//检查用户是否登录
//拦截全部请求
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，至此通配符，专门用来进行路径匹配的类
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        1.获取本次请求的url;||backend/index.html
        String requestUrl = request.getRequestURI();
        log.info("拦截到请求:{}",requestUrl);

       /* 页面请求静态资源不用处理直接放行即可；
        主要拦截的是对controller访问的请求*/
        String[] urls = new String[]{//定义不需要处理的请求路径
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

//        2.判断本次请求是否需要处理
        boolean check=check(urls,requestUrl);
//        3.如果不需要处理，则直接放行
        if(check){
            log.info("本次请求'{}'不需要处理",requestUrl);
            //放行
            filterChain.doFilter(request, response);
            return;
        }


//        后端员工登录
        //4-1、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }


//        前台登录
        //4-2、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }



//        5.如果未登录则直接返回未登录结果,通过输出流方式向客户端界面响应数据
        //其他功能在/backend/js/request.js中实现，如跳转登录界面等
        log.warn("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        //        {}相当于一个占位符,动态的获取值
//        log.warn("拦截到请求: {}", request.getRequestURI());

    }


    //路径匹配，检查本次请求是否需要放行
    public boolean check(String[] urls,String requestUrl){
        for (String url:urls) {
            boolean match = PATH_MATCHER.match(url, requestUrl);
            if (match){
                return true;
            }
        }
        return false;
    }

}
