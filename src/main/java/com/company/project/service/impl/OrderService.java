package com.company.project.service.impl;

import com.company.project.entity.Order;
import com.company.project.mapper.OrderMapper;
import com.company.project.service.IOrderService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/8
 */
@Service
public class OrderService implements IOrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderMapper orderMapper;

    public void start(){
        redisTemplate.opsForValue().set("num","100");
    }

    @SneakyThrows
    @Override
    public String addOrder(Order order) {
        Integer uid = order.getUid();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock","true");
        if(lock) {
            if (Integer.parseInt((redisTemplate.opsForValue().get("num").toString())) > 0) {
                order.setNum("1");
                order.setPrice("40");
                redisTemplate.opsForValue().set("num", String.valueOf(Integer.parseInt(String.valueOf(redisTemplate.opsForValue().get("num"))) - 1));
                orderMapper.insertOrder(order);
                redisTemplate.delete("lock");
                return "uid:" + uid + ",msg:" + redisTemplate.opsForValue().get("num");
            } else {
                redisTemplate.delete("lock");
                return "uid:" + uid + ",msg:sold out!";
            }
        } else {
            Thread.sleep(100);
            return addOrder(order);
        }
    }
}
