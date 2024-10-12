package com.company.project.controller;

import com.alibaba.fastjson2.JSON;
import com.company.project.entity.DutyLog;
import com.company.project.service.IDutyLogService;
import com.company.project.service.impl.DutyLogServiceImpl;
import com.company.project.utils.DbUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

        IDutyLogService dutyLogService = new DutyLogServiceImpl();
        ResultSet totalDutyLogRs = DbUtil.getTotalDutyLogResultSet("2024-09-%");
        ResultSet dutyLogRs = DbUtil.getDutyLogResultSet(name,"2024-09-%");
        List<DutyLog> dutyLogList = dutyLogService.calculateAttendanceList(totalDutyLogRs);
        Map<String,Map> dailyWorkTime = dutyLogService.calculateDailyWorkTime(dutyLogRs);
        DutyLog totalWorkTime = dutyLogService.calculateTotalWorkTime(dutyLogRs);

        System.out.println(JSON.toJSONString(dutyLogList));
        System.out.println(JSON.toJSONString(dailyWorkTime));
        System.out.println(JSON.toJSONString(totalWorkTime));

        return JSON.toJSONString(dutyLogService.calculateDailyWorkTime(dutyLogRs));
    }
}
