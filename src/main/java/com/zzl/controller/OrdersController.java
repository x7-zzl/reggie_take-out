package com.zzl.controller;

import com.zzl.common.R;
import com.zzl.entity.Orders;
import com.zzl.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrderService orderService;

    //用户下单功能，支付
    @PostMapping("/submit")
    public R<String> submit(Orders orders){
        log.info("订单数据:{}",orders);
        orderService.submit(orders);

        return R.success("下单成功");
    }
}
