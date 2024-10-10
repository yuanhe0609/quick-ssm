package com.company.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private int oid;
    private int uid;
    private String num;
    private String price;
}
