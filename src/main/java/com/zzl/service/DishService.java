package com.zzl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.common.R;
import com.zzl.dto.DishDTO;
import com.zzl.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish/dish_flavor
    public void saveWithFlavor(DishDTO dishDTO);

    //根据id查询菜品信息和口味信息
    public DishDTO getByIdWithFlavor(Long id);

    //更新菜品信息，更新对应的口味信息
    public void updateWithFlavor(DishDTO dishDTO);

    public void updateStatus(Integer status, List<Long> ids);

    public void deleteDish(List<Long> ids);
}
