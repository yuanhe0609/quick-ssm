package com.company.project.controller;

import com.alibaba.fastjson2.JSON;
import com.company.project.entity.DailyDutyLogEntity;
import com.company.project.entity.EmployeeSalaryEntity;
import com.company.project.entity.TotalDutyLogEntity;
import com.company.project.service.DutyLogCalculate;
import com.company.project.service.EmployeeSalaryCalculate;
import com.company.project.utils.DbUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/9
 */
@RestController
@Api(value = "DutyLogController")
@Slf4j
public class DutyLogController {
    @Autowired
    private DutyLogCalculate dutyLogCalculate;
    @Autowired
    private EmployeeSalaryCalculate employeeSalaryCalculate;

    @RequestMapping(value = "/calculate/{year}/{month}", produces = "text/html;charset=utf-8")
    @ApiOperation(value = "计算员工出勤时间以及工资")
    public String calculateLog(@PathVariable Integer year, @PathVariable Integer month) throws SQLException, ParseException {
        String currentMonth = "";
        String lastMonth = "";
        if(month<10){
            currentMonth = "0"+month;
            lastMonth = "0"+(month-1);
        }else if(month == 10){
            currentMonth = ""+month;
            lastMonth = "0"+(month-1);
        }else{
            currentMonth = ""+month;
            lastMonth = "" + (month-1);
        }

        //获取出勤表数据
        ResultSet totalDutyLogResultSet = DbUtil.getTotalDutyLogResultSet(year+"-"+lastMonth+"-26",year+"-"+currentMonth+"-25");
        //根据出勤表计算每日出勤时间
        List<DailyDutyLogEntity> dailyWorkTimeList = dutyLogCalculate.calculateAttendanceList(totalDutyLogResultSet);
        //根据每日出勤时间计算每月出勤时间
        Map<String, List<DailyDutyLogEntity>>  classifyByNameMap = dailyWorkTimeList.stream().collect(Collectors.groupingBy(DailyDutyLogEntity::getName));
        List<TotalDutyLogEntity> monthlyWorkTimeList = dutyLogCalculate.calculateTotalWorkTime(classifyByNameMap);
        //根据每月出勤时间计算每月工资
        List<EmployeeSalaryEntity> salaryList = employeeSalaryCalculate.calculateEmployeeSalary(monthlyWorkTimeList,year,month);

        return JSON.toJSONString(salaryList);
    }
}
