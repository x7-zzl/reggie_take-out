package com.zzl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.CustomException;
import com.zzl.entity.Category;
import com.zzl.entity.Dish;
import com.zzl.entity.Setmeal;
import com.zzl.mapper.CategoryMapper;
import com.zzl.service.CategoryService;
import com.zzl.service.DishService;
import com.zzl.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;

    /**
     * 根据id删除分类，删除之前需要先进行判断
     *
     * @param id
     */
    @Override
    public void removeById(Long id) {
        //        select count(*) from dish where id=?
        //查询当前分类是否关联了菜品,如果已经关联，抛出一个业务异常

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);

        int countDish = dishService.count(dishLambdaQueryWrapper);
        if (countDish > 0) {//说明该分类关联了菜品
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int countSetMeal = setMealService.count(setmealLambdaQueryWrapper);
        if (countSetMeal > 0) {
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }


        //说明都没有关联。可以直接删除
        super.removeById(id);
    }


}
