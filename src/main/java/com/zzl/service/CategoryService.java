package com.zzl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.entity.Category;

public interface CategoryService extends IService<Category> {
    public void removeById(Long id);
}
