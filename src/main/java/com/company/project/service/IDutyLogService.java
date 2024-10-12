package com.company.project.service;
import com.company.project.entity.DailyDutyLog;
import com.company.project.entity.TotalDutyLog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

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
    /**
     * @description 用于数据库操作的平日出勤时间字段的字段名
     * @type String
     * @default prcq
     * */
    String SQL_WORK_TIME_ON_WEEKDAYS = "prcq";
    /**
     * @description 用于数据库操作的平日加班时间字段的字段名
     * @type String
     * @default prjb
     * */
    String SQL_OVER_TIME_ON_WEEKDAYS = "prjb";
    /**
     * @description 用于数据库操作的周末出勤时间字段的字段名
     * @type String
     * @default zmcq
     * */
    String SQL_WORK_TIME_ON_WEEKENDS = "zmcq";
    /**
     * @description 用于数据库操作的周末加班时间字段的字段名
     * @type String
     * @default zmjb
     * */
    String SQL_OVER_TIME_ON_WEEKENDS = "zmjb";
    /**
     * @description 用于数据库操作的节日出勤时间字段的字段名
     * @type String
     * @default jrcq
     * */
    String SQL_WORK_TIME_ON_HOLIDAY = "jrcq";
    /**
     * @description 用于数据库操作的节日加班时间字段的字段名
     * @type String
     * @default jrjb
     * */
    String SQL_OVER_TIME_ON_HOLIDAY = "jrjb";
    /**
     * @description 更新出勤表
     * @param dutyLogResultSet ResultSet
     * @return result List<DutyLog>
     * */
    public List<DailyDutyLog> calculateAttendanceList(ResultSet dutyLogResultSet) throws SQLException;
    /**
     * @description 计算每月出勤,加班时间
     * @param dutyLogResultSet ResultSet (因需要反复利用ResultSet,需在prepareStatement设置ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
     * @return result List<DutyLog>
     */
    public Map<String,Map> calculateDailyWorkTime(ResultSet dutyLogResultSet) throws SQLException;
    public Map<String,Map> calculateDailyWorkTime(List<DailyDutyLog> dutyLogList) throws SQLException;
    public TotalDutyLog calculateTotalWorkTime(ResultSet dutyLogResultSet) throws SQLException;
}
