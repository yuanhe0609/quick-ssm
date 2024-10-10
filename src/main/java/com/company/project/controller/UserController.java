package com.company.project.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.company.project.entity.User;
import com.company.project.mapper.UserMapper;
import com.company.project.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping("/register")
    @ApiOperation(value = "根据name,password注册", httpMethod = "POST", notes = "用户注册", response = java.lang.String.class)
    public String register(){

        User user = new User();
        user.setName("test");
        user.setPassword("123456");

        userService.register(user);

        return JSON.toJSONString(user);
    }
    @RequestMapping("/login")
    @ApiOperation(value = "根据name,password登录验证",notes="用户登录", httpMethod = "POST", response = java.lang.String.class)
    public String login(){

        return JSON.toJSONString(userService.login());
    }
}
