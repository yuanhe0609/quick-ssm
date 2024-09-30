package com.company.project.service.impl;

import com.company.project.entity.User;
import com.company.project.mapper.UserMapper;
import com.company.project.service.IUserService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


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

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Integer register(User user) {
        Integer row = userMapper.register(user);
        log.info("update row:",row);
        return row;
    }
}
