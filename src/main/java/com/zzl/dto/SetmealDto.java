package com.zzl.dto;

import com.zzl.entity.Setmeal;
import com.zzl.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
