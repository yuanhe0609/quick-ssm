package com.company.project.service;

import com.company.project.entity.DutyLog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/9
 */
public interface IDutyLogService {
    public List<DutyLog> updateAttendanceList(ResultSet dutyLogResultSet,ResultSet holidayResultSet) throws SQLException;

    public void calculateMonthWorkTime();
}
