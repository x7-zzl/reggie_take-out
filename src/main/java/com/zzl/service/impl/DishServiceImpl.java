package com.zzl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.common.CustomException;
import com.zzl.dto.DishDTO;
import com.zzl.entity.Dish;
import com.zzl.entity.DishFlavor;
import com.zzl.mapper.DishMapper;
import com.zzl.service.DishFlavorService;
import com.zzl.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish/dish_flavor
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        //保存菜品的基本信息到菜品表中

        this.save(dishDTO);

        Long dishId = dishDTO.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //可以用集合遍历

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味到菜品口味表dish_flavor中

        dishFlavorService.saveBatch(flavors);
    }

    //根据id查询菜品信息和口味信息
    @Override
    public DishDTO getByIdWithFlavor(Long id) {
        //根据id查询菜品信息
        Dish dish = this.getById(id);

        DishDTO dishDTO = new DishDTO();
        BeanUtils.copyProperties(dish, dishDTO);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDTO.setFlavors(list);

        return dishDTO;
    }

    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //更新dish菜品表
        this.updateById(dishDTO);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();;
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDTO.getId());
        dishFlavorService.remove(lambdaQueryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDTO.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDTO.getId());
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味到菜品口味表dish_flavor中
        dishFlavorService.saveBatch(flavors);

    }


    //修改状态成功
    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Dish::getId, ids);
        List<Dish> list = this.list(queryWrapper);

//        List<Dish> list=new ArrayList<>();
//        for (long id:ids) {
//            Dish dish=this.getById(id);
//            list.add(dish);
//        }


        for (Dish dish : list) {
            if (dish != null) {
                dish.setStatus(status);
                this.updateById(dish);
            }
        }
    }



//    删除菜品
    @Override
    @Transactional
    public void deleteDish(List<Long> ids) {
        // 查菜品的状态，是否可以删除（）
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.in(Dish::getId, ids);
        dishQueryWrapper.eq(Dish::getStatus, 1);

        int count = this.count(dishQueryWrapper);
        if(count > 0) {
            throw new CustomException("菜品正在售卖中！不能删除！");
        }

        // 删除dish
        this.removeByIds(ids);

        // 删除dish_flavor
        LambdaQueryWrapper<DishFlavor> dfQueryWrapper = new LambdaQueryWrapper<>();
        dfQueryWrapper.in(DishFlavor::getDishId, ids);

        dishFlavorService.remove(dfQueryWrapper);

    }
}
