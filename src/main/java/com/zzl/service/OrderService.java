package com.zzl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.entity.Orders;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
}
