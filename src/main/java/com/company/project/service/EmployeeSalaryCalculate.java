package com.company.project.service;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/11
 */
public interface EmployeeSalaryCalculate {

    Integer BASIC_MONTHLY_SALARY = 2200;

    Integer calculateEmployeeSalary(ResultSet dailyDutyLog) throws SQLException;
}
