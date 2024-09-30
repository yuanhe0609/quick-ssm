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
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping("/register")
    @ApiOperation(value = "根据id查询学生信息", notes = "查询学生", response = java.lang.String.class)
    public String login(){

        User user = new User();
        user.setName("test");
        user.setPassword("123456");

        return JSON.toJSONString(userMapper.register(user));
    }
}
