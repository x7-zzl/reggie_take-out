package com.zzl.dto;

import com.zzl.entity.Dish;
import com.zzl.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class DishDTO extends Dish {

    private List<DishFlavor> flavors=new ArrayList<>();
    private String categoryName;

    private Integer copies;
}
