package com.zzl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.common.R;
import com.zzl.entity.Employee;
import com.zzl.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    //员工登录
    @PostMapping("/login")

    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

//        1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

//        2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

//        3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登录失败，用户名不存在");
        }
//        4、密码比对，如果不一一对应返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败，密码错误");
        }
//        5、查看员工状态, 如果为已非用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
//        6、登录成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }


    //    员工退出功能
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
//        1.清理session中的用户id
        request.getSession().removeAttribute("employee");

//        2.返回结果，跳转回登录界面
        return R.success("退出成功");
    }


    //新增员工功能
    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工,员工信息：{}", employee.toString());

        //设置初始密码，需要加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));


       /* employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //获取当前用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/

        employeeService.save(employee);
        return R.success("新增员工成功");
    }


    //员工信息分页功能
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("当前页码={},页数={},name={}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<Employee>();
        //添加过滤条件，判断name是否==null
        lambdaQueryWrapper.like(Strings.isNotEmpty(name), Employee::getName, name);
        //添加排序条件，根据最后的更新时间
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }


    //修改属性功能
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());

        /*ong empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());*/

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

//    修改员工信息
    @GetMapping("/{id}")
    public R<Employee>  getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到相关信息");
    }
}
