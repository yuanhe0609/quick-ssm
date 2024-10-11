package com.company.project.service;

import com.company.project.entity.DutyLog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/9
 */
public interface IDutyLogService {
    /**
     * @description 定义日期格式
     * @type SimpleDateFormat
     * @default yyyy-MM-dd HH:mm:ss
     * */
    final SimpleDateFormat SDF_WITH_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * @description 定义日期格式
     * @type SimpleDateFormat
     * @default yyyy-MM-dd
     * */
    final SimpleDateFormat SDF_NO_TIME = new SimpleDateFormat("yyyy-MM-dd");
    //
    /**
     * @description 定义浮点数小数点个数
     * @type DecimalFormat
     * @default 1位数
     * */
    final DecimalFormat DF = new DecimalFormat("0.0");

    /**
     * @description 用于数据库操作的生产人员姓名字段的字段名
     * @type String
     * @default xm
     * */
    String SQL_NAME = "xm";
    /**
     * @description 用于数据库操作的生产人员身份证号字段的字段名
     * @type String
     * @default sfzh
     * */
    String SQL_IDNUM = "sfzh";
    /**
     * @description 用于数据库操作的生产人员当日上岗时间字段的字段名
     * @type String
     * @default sgsj
     * */
    String SQL_ON_DUTY_TIME = "sgsj";
    /**
     * @description 用于数据库操作的生产人员离岗时间字段的字段名
     * @type String
     * @default lgsj
     * */
    String SQL_OFF_DUTY_TIME = "lgsj";
    /**
     * @description 用于数据库操作的休假日类型字段的字段名
     * @type String
     * @default changetype
     * */
    String SQL_HOLIDAY_TYPE = "changetype";

    String SQL_WORK_TIME_ON_WEEKDAYS = "prcq";

    String SQL_OVER_TIME_ON_WEEKDAYS = "prjb";

    String SQL_WORK_TIME_ON_WEEKENDS = "zmcq";

    String SQL_OVER_TIME_ON_WEEKENDS = "zmjb";

    String SQL_WORK_TIME_ON_HOLIDAY = "jrcq";

    String SQL_OVER_TIME_ON_HOLIDAY = "jrjb";
    public List<DutyLog> updateAttendanceList(ResultSet dutyLogResultSet) throws SQLException;

    public List calculateMonthWorkTime(ResultSet dutyLogResultSet) throws SQLException;
}
