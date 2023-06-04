package com.zzl.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzl.common.BaseContext;
import com.zzl.common.R;
import com.zzl.entity.ShoppingCart;
import com.zzl.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    //显示购物车数据
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车:");

        Long currentId = BaseContext.getCurrentId();

        //根据用户id查询购物车表的内容
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        //按照创建时间进行排序
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    //添加到购物车
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据:{}", shoppingCart);

        //设置用户id，当前购物车的用户id是什么
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            //添加的是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        //如果已经存在，就在原来的基础上加1
        if (one != null) {
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);

        } else {//如果不存在，则添加到购物车，默认是1
            shoppingCart.setNumber(1);

            //添加创建时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            //将当前对象设置给one
            one = shoppingCart;
        }

        return R.success(one);
    }



    @PostMapping("/sub")
    //减少购物车的菜品数量
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        //查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        if (dishId != null) {
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }


        //获取该用户购物车中的数据
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        //获取菜品或者套餐的数量
        Integer number = one.getNumber();

        if (number<=1){//数量为1时，减少直接删除该菜品
            shoppingCartService.remove(queryWrapper);
        }else {//数量大于1时，菜品数量减1
            number--;
            one.setNumber(number);
            shoppingCartService.updateById(one);
        }

        return R.success(one) ;
    }


    //清空购物车
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }

}
