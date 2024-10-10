package com.company.project.controller;

import com.alibaba.fastjson2.JSON;
import com.company.project.utils.JsonResult;
import com.company.project.entity.Order;
import com.company.project.service.impl.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/8
 */
@RestController
@Api(value = "mainController")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/buy/{uid}")
    @ApiOperation(value = "根据uid进行抢购", notes = "抢购",httpMethod = "POST", response = java.lang.String.class)
    public String buy(@PathVariable Integer uid) {
        Order order = new Order();
        order.setUid(uid);
        String row = orderService.addOrder(order);
        System.out.println(row);
        return JSON.toJSONString(new JsonResult(200,row));
    }

    @RequestMapping("/start")
    @ApiOperation(value = "开始抢购活动", notes = "开始" , httpMethod = "POST",response = java.lang.String.class)
    public String start() {
        orderService.start();
        return JSON.toJSONString(new JsonResult(200));
    }
}
