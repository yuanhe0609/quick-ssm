package com.company.project.entity;

import lombok.Data;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/9
 */
@Data
public class Employee {
    private String name;
    private String iDNumber;
    private Float totalWorkTime;
    private Float totalWorkDay;
    private Float nightWorkTime;
    private Float weekdayWorkTime;
    private Float weekdayOverTime;
    private Float weekendWorkTime;
    private Float weekendOverTime;
}
