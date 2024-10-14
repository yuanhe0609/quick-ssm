package com.company.project.controller;

import com.alibaba.fastjson2.JSON;
import com.company.project.entity.DailyDutyLogEntity;
import com.company.project.entity.EmployeeSalaryEntity;
import com.company.project.entity.TotalDutyLogEntity;
import com.company.project.service.DutyLogCalculateFormula;
import com.company.project.service.EmployeeSalaryCalculate;
import com.company.project.service.impl.DutyLogCalculateImpl;
import com.company.project.service.impl.EmployeeSalaryCalculateImpl;
import com.company.project.utils.DbUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
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
    @RequestMapping("/calculate_log/{month}")
    public String calculateLog(@PathVariable Integer month) throws SQLException, ParseException {
        String nowMonth = "";
        if(month<10){
            nowMonth = "0"+month;
        }else{
            nowMonth = ""+month;
        }

        DutyLogCalculateFormula dutyLogService = new DutyLogCalculateImpl();
        EmployeeSalaryCalculate employeeSalaryCalculate = new EmployeeSalaryCalculateImpl();
        //获取出勤表数据
        ResultSet totalDutyLogResultSet = DbUtil.getTotalDutyLogResultSet("2024-09-26","2024-10-25");
        //根据出勤表计算每日出勤时间
        List<DailyDutyLogEntity> dailyWorkTimeList = dutyLogService.calculateAttendanceList(totalDutyLogResultSet);
        //根据每日出勤时间计算每月出勤时间
        Map<String, List<DailyDutyLogEntity>>  classifyByNameMap = dailyWorkTimeList.stream().collect(Collectors.groupingBy(DailyDutyLogEntity::getName));
        List<TotalDutyLogEntity> monthlyWorkTimeList = dutyLogService.calculateTotalWorkTime(classifyByNameMap);
        //根据每月出勤时间计算每月工资
        List<EmployeeSalaryEntity> salaryList = employeeSalaryCalculate.calculateEmployeeSalary(monthlyWorkTimeList,2024,10);

        return JSON.toJSONString(salaryList);
    }
}
