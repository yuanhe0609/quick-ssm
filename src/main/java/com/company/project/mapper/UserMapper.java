package com.company.project.mapper;

import com.company.project.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/9/24
 */
@Mapper
public interface UserMapper {
    Integer register(User user);
}
