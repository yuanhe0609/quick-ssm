package com.company.project.controller;

import com.alibaba.fastjson2.JSON;
import com.company.project.service.IDutyLogService;
import com.company.project.service.impl.DutyLogService;
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
import java.util.Date;
import java.util.List;

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
    private DutyLogService dutyLogService;


    @RequestMapping("/get_log/{name}")
    public String getLog(@PathVariable String name) throws SQLException, ParseException {
        dutyLogService.calculateWorkTime();
        String dateString = "2024-09";
        Date date= new SimpleDateFormat("yyyy-MM").parse(dateString);
        dutyLogService.calculateMonthWorkTime(date,name);
        return "";
    }
    @RequestMapping("/get_log")
    public String getLog() throws SQLException, ParseException {
        IDutyLogService dutyLogService = new DutyLogServiceImpl();

        Connection conn = DbUtil.getConnection();
        String selectDutyLogSql = "select xm,sfzh,bc,sgsj,lgsj,id from uf_duty_log order by id";
        PreparedStatement psSelectDutyLog = conn.prepareStatement(selectDutyLogSql);
        ResultSet dutyLogRs = psSelectDutyLog.executeQuery();

        List result = dutyLogService.updateAttendanceList(dutyLogRs);
        return JSON.toJSONString(result);
    }
    @RequestMapping("/calculate_log/{name}")
    public String calculateLog(@PathVariable String name) throws SQLException, ParseException {
        IDutyLogService dutyLogService = new DutyLogServiceImpl();

        Connection conn = DbUtil.getConnection();
        String selectSql = "select * from uf_duty_log where modedatacreatedate like ? and xm = ? order by sgsj";
        PreparedStatement psSelect = conn.prepareStatement(selectSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        psSelect.setObject(1,"2024-09-%");
        psSelect.setObject(2,name);
        ResultSet dutyLogRs = psSelect.executeQuery();
        dutyLogService.calculateMonthWorkTime(dutyLogRs);

        return "";
    }
}
