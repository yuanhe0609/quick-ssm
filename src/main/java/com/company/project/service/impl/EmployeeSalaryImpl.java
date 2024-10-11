package com.company.project.service.impl;
import com.company.project.service.IEmployeeSalary;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/11
 */
@Service
public class EmployeeSalaryImpl extends BaseCalculateFormula implements IEmployeeSalary {

    @Override
    public Integer calculateEmployeeSalary(ResultSet dailyLog) throws SQLException {
        while(dailyLog.next()){

        }
        return 0;
    }
}
