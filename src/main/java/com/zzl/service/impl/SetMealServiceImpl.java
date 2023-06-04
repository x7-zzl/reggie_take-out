package com.zzl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.CustomException;
import com.zzl.dto.SetmealDto;
import com.zzl.entity.Setmeal;
import com.zzl.entity.SetmealDish;
import com.zzl.mapper.SetMealMapper;
import com.zzl.service.SetMealDishService;
import com.zzl.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;


    //新增套餐，同时添加菜品和套餐之间的关系；
    //即对两个表setmeal.setmealdish执行插入操作

    //添加事务注解，要么全成功，要么全失败
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //先添加到套餐表setmeal
        this.save(setmealDto);

        //添加套餐和菜品的关系到表setmeldish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item) -> {

            //将setmealdish表的套餐id  setmealid设置为套餐的id
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setMealDishService.saveBatch(setmealDishes);

    }


    @Transactional
    //删除套餐，同时删除菜品和套餐的关联数据
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，决定是否删除

        //设置条件
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //删除ids集合，批量删除
        //in()方法用于查询某个字段在给定值列表中的记录
        lambdaQueryWrapper.in(Setmeal::getId, ids);
        //状态为1时不能删除
        lambdaQueryWrapper.eq(Setmeal::getStatus, 1);

        // 不能删除的话抛异常不能删除（select count(*）from setmeal where id in ?)
        int count = this.count(lambdaQueryWrapper);

        if (count > 0) {
            throw new CustomException("菜品售卖中，不能删除");
        }

        //可以删除，先删除套餐表
        this.removeByIds(ids);

        //然后删除套餐和菜品之间的关系
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setMealDishService.remove(setmealDishLambdaQueryWrapper);
    }

    @Override
    public void updateStatus(Integer status, List<Long> ids) {

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ids != null, Setmeal::getId, ids);
        List<Setmeal> list = this.list(lambdaQueryWrapper);

        for (Setmeal s : list) {
            if (s != null) {
                s.setStatus(status);
                this.updateById(s);
            }
        }
    }


}
