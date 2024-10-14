package com.company.project.service.impl;
import com.alibaba.fastjson2.JSON;
import com.company.project.entity.TotalDutyLog;
import com.company.project.service.EmployeeSalaryCalculate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/11
 */
@Service
public class EmployeeSalaryCalculateImpl extends BaseCalculate implements EmployeeSalaryCalculate {
    @Override
    public Integer calculateEmployeeSalary(ResultSet totalDutyLogResultSet) throws SQLException {
        return 0;
    }
}
