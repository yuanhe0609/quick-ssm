package com.company.project.controller;

import com.alibaba.fastjson2.JSON;
import com.company.project.entity.DailyDutyLog;
import com.company.project.entity.TotalDutyLog;
import com.company.project.service.DutyLogCalculateFormula;
import com.company.project.service.impl.DutyLogCalculateImpl;
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
    @RequestMapping("/calculate_log/{name}")
    public String calculateLog(@PathVariable String name) throws SQLException, ParseException {

        DutyLogCalculateFormula dutyLogService = new DutyLogCalculateImpl();
        ResultSet totalDutyLogRs = DbUtil.getTotalDutyLogResultSet("2024-09-%");
        List<DailyDutyLog> dailyDutyLogList = dutyLogService.calculateAttendanceList(totalDutyLogRs);
        Map<String, List<DailyDutyLog>> classMap = dailyDutyLogList.stream().collect(Collectors.groupingBy(DailyDutyLog::getName));
//        List<Map<String,Map>> list = dutyLogService.calculateDailyWorkTime(classMap);
        List<TotalDutyLog> list = dutyLogService.calculateTotalWorkTime(classMap);
        return JSON.toJSONString(list);
    }
}
