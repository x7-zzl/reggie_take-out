package com.zzl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.dto.SetmealDto;
import com.zzl.entity.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {

    //新增套餐，同时添加菜品和套餐之间的关系；
    //即对两个表setmeal.setmealdish执行插入操作
    public void saveWithDish(SetmealDto setmealDto);

    //删除套餐，同时删除菜品和套餐的关联数据
    public void removeWithDish(List<Long> ids);

    public void updateStatus(Integer status, List<Long> ids);
}
