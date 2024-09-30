package com.company.project.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.company.project.entity.User;
import com.company.project.mapper.UserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/9/24
 */
@RestController
@Api(value = "userController")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisTemplate redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @RequestMapping("/login")
    @ApiOperation(value = "根据id查询学生信息", notes = "查询学生", response = java.lang.String.class)
    public String login(){
        redisTemplate.opsForValue().set("login", "1");
        Object msg = redisTemplate.opsForValue().get("login");
        log.info("slf4j print info msg:{}",msg);
        User user = new User();
        user.setName("test01");
        userMapper.addUser(user);
        return JSON.toJSONString(user);
    }
}
