package com.company.project.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.company.project.entity.User;
import com.company.project.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/9/24
 */
@RestController
public class UserController {

    @Autowired
    UserMapper userMapper;

    @RequestMapping("/login")
    public String login(){

        User user = new User();
        user.setName("test01");
        userMapper.addUser(user);
        return JSON.toJSONString(user);
    }
}
