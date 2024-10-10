package com.company.project.mapper;

import com.company.project.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/8
 */

@Mapper
public interface OrderMapper {
    Integer insertOrder(Order order);
}
