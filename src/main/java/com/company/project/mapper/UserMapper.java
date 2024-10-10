package com.company.project.mapper;

import com.company.project.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/9/24
 */
@Mapper
public interface UserMapper {
    Integer register(User user);

    List<User> getUser();
}
