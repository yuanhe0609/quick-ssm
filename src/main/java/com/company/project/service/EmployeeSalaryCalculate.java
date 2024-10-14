package com.company.project.service;

import com.company.project.entity.TotalDutyLog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/11
 */
public interface EmployeeSalaryCalculate {
    /**
     * @description 基本工资
     * @type Integer
     * @default 2200
     * */
    Integer BASIC_MONTHLY_SALARY = 2200;

    Integer calculateEmployeeSalary(ResultSet totalDutyLogResultSet) throws SQLException;
}
