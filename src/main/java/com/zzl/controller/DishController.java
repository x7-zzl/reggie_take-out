package com.zzl.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.common.R;
import com.zzl.dto.DishDTO;
import com.zzl.entity.Category;
import com.zzl.entity.Dish;
import com.zzl.entity.DishFlavor;
import com.zzl.service.CategoryService;
import com.zzl.service.DishFlavorService;
import com.zzl.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDTO dishDTO) {
        log.info(dishDTO.toString());
        dishService.saveWithFlavor(dishDTO);

        return R.success("新增菜品成功");
    }


    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //分页构造器对象
        Page<Dish> dishPage = new Page<Dish>(page, pageSize);

        Page<DishDTO> dishDTOPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Strings.isNotEmpty(name), Dish::getName, name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(dishPage, lambdaQueryWrapper);

//        总体思路是先拷贝分页数据，然后Dish的records排除，因为没有categoryName;
//        然后遍历Dish的records,加入categoryName，再返回到DishDTO对象中

        /**   BeanUtils.copyProperties() 对象拷贝
         *    dishPage：源数据对象    dishDTOPage:目标数据对象    忽略records
         */

        //        第一次拷贝是拷贝的分页信息，其实就是拷贝的totals、page、pagesize这些数据，
//        并没有拷贝实际的菜品属性，因为你菜品属性里并没有categoryName
        BeanUtils.copyProperties(dishPage, dishDTOPage, "records");
        //records是对象的记录集合
        //获取Dish的records
        List<Dish> records = dishPage.getRecords();

//        遍历records赋值给listDishDTO
        List<DishDTO> listDishDTO = records.stream().map((item) -> {
            DishDTO dishDTO = new DishDTO();
            //对象拷贝
            BeanUtils.copyProperties(item, dishDTO);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String tName = category.getName();
                dishDTO.setCategoryName(tName);
            }

            return dishDTO;
        }).collect(Collectors.toList());

        //设置DishDTO records
        dishDTOPage.setRecords(listDishDTO);

        return R.success(dishDTOPage);
    }


    @GetMapping("/{id}")
    public R<DishDTO> get(@PathVariable Long id) {
        DishDTO dishDTO = dishService.getByIdWithFlavor(id);

        return R.success(dishDTO);
    }


    //修改菜品信息
    @PutMapping
    public R<String> update(@RequestBody DishDTO dishDTO) {
        log.info(dishDTO.toString());

        dishService.updateWithFlavor(dishDTO);
        return R.success("修改菜品成功");
    }


    //启售停售功能
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {

        dishService.updateStatus(status, ids);
        return R.success("菜品状态修改成功");
    }


    //    删除菜品
    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids) {
        log.info("ids:{}", ids);
        dishService.deleteDish(ids);

        return R.success("删除菜品成功！");
    }

   /* //根据条件查询对应的菜品数据
    @GetMapping("/list")
    public R<List<Dish>>  list(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询状态是1的，启售
        lambdaQueryWrapper.eq(Dish::getStatus,1);
//        根据前端传来的categoryId查询
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //模糊查询，输入菜品名称搜素
        lambdaQueryWrapper.like(Strings.isNotEmpty(dish.getName()), Dish::getName, dish.getName());
        //排序
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);


        List<Dish> list=dishService.list(lambdaQueryWrapper);
        return R.success(list);
    }
*/


    @GetMapping("/list")
    public R<List<DishDTO>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDTO> dishDtoList = list.stream().map((item) -> {
            DishDTO dishDto = new DishDTO();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}
