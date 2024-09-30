package com.company.project.service;

import com.company.project.entity.User;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/9/25
 */
public interface IUserService {

    Integer register(User user);
}
