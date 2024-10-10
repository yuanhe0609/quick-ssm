package com.company.project.service.impl;

import com.company.project.entity.User;
import com.company.project.mapper.UserMapper;
import com.company.project.service.IUserService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/9/24
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Integer register(User user) {

        Integer row = userMapper.register(user);

        return row;
    }

    @Override
    public List<User> login() {
        return userMapper.getUser();
    }
}
