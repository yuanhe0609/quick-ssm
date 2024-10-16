package com.company.project.service;

import com.company.project.entity.EmployeeSalaryEntity;
import com.company.project.entity.TotalDutyLogEntity;
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
    /**
     * @param totalDutyLogResultSet ResultSet
     * @param year Integer
     * @param month Integer
     * @return result List<EmployeeSalaryEntity>
     * @description 计算所有人每月工资
     */
    List<EmployeeSalaryEntity> calculateEmployeeSalary(ResultSet totalDutyLogResultSet,Integer year,Integer month) throws SQLException;
    /**
     * @param totalDutyLogResultSet ResultSet
     * @return result List<EmployeeSalaryEntity>
     * @description 计算所有人每月工资
     */
    List<EmployeeSalaryEntity> calculateEmployeeSalary(ResultSet totalDutyLogResultSet) throws SQLException;
    /**
     * @param totalDutyLogEntityList List<TotalDutyLogEntity>
     * @param year Integer
     * @param month Integer
     * @return result List<EmployeeSalaryEntity>
     * @description 计算所有人每月工资
     */
    List<EmployeeSalaryEntity> calculateEmployeeSalary(List<TotalDutyLogEntity> totalDutyLogEntityList,Integer year,Integer month) throws SQLException;
}
