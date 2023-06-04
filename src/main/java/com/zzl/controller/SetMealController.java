package com.zzl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.common.R;
import com.zzl.dto.DishDTO;
import com.zzl.dto.SetmealDto;
import com.zzl.entity.Category;
import com.zzl.entity.Setmeal;
import com.zzl.entity.SetmealDish;
import com.zzl.service.CategoryService;
import com.zzl.service.SetMealDishService;
import com.zzl.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//套餐管理
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;

    @Autowired
    private SetMealDishService setMealDishService;


    @Autowired
    private CategoryService categoryService;

    //新增套餐
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息:{}",setmealDto.toString());
        setMealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");

    }


    //套餐分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页
        Page<Setmeal> setmealPage=new Page<>(page,pageSize);

        //数据库setmeal存的是套餐分类的id而不是name,所以无法显示数据
        //所以使用dto对象
        Page<SetmealDto> dtoPage=new Page<>();


        //模糊查询
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Strings.isNotEmpty(name),Setmeal::getName,name);

        //按更新时间排序
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setMealService.page(setmealPage,lambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(setmealPage,dtoPage,"records");

        //遍历records集合，里面是setmeal套餐信息
        List<Setmeal> records = setmealPage.getRecords();

        //dto对象中还缺少分类名
        List<SetmealDto> collect = records.stream().map((item) -> {
            //定义dto对象，作为返回值
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据id获得对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                //根据对象获取分类名称
                String categoryName = category.getName();
                //设置分类名
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //设置dto分页的records数据
        dtoPage.setRecords(collect);

        //返回dto分页的数据
        return R.success(dtoPage);
    }

    //删除套餐和套餐与菜品的联系
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setMealService.removeWithDish(ids);

        return R.success("套餐数据删除成功");
    }


    //启售停售功能
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {

        setMealService.updateStatus(status, ids);
        return R.success("套餐状态修改成功");
    }


    //前台套餐展示界面
    @GetMapping("/list")
    public R<List<Setmeal>>  list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setMealService.list(lambdaQueryWrapper);

        return R.success(list);
    }

}
