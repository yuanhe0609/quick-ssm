package com.company.project.service.impl;

import com.company.project.service.DutyLogCalculate;
import com.company.project.utils.DbUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/11
 */
public class BaseCalculate {
    /**
     * @description 用于数据库操作的生产人员姓名字段的字段名
     * @type String
     * @default xm
     * */
    static String SQL_NAME = "xm";
    /**
     * @description 用于数据库操作的生产人员身份证号字段的字段名
     * @type String
     * @default sfzh
     * */
    static String SQL_IDNUM = "sfzh";
    /**
     * @description 用于数据库操作的生产人员当日上岗时间字段的字段名
     * @type String
     * @default sgsj
     * */
    static String SQL_WORK_TYPE = "bc";
    /**
     * @description 用于数据库操作的生产人员当日上岗时间字段的字段名
     * @type String
     * @default sgsj
     * */
    static String SQL_MONTH = "yf";
    /**
     * @description 用于数据库操作的生产人员当日上岗时间字段的字段名
     * @type String
     * @default sgsj
     * */
    static  String SQL_ON_DUTY_TIME = "sgsj";
    /**
     * @description 用于数据库操作的生产人员离岗时间字段的字段名
     * @type String
     * @default lgsj
     * */
    static  String SQL_OFF_DUTY_TIME = "lgsj";
    /**
     * @description 用于数据库操作的休假日类型字段的字段名
     * @type String
     * @default changetype
     * */
    static String SQL_HOLIDAY_TYPE = "changetype";
    /**
     * @description 用于数据库操作的平日出勤时间字段的字段名
     * @type String
     * @default prcq
     * */
    static  String SQL_WORK_TIME_ON_WEEKDAYS = "prcq";
    /**
     * @description 用于数据库操作的月总平日出勤时间字段的字段名
     * @type String
     * @default prcqxs
     * */
    static  String SQL_TOTAL_WORK_TIME_ON_WEEKDAYS = "prcqxs";
    /**
     * @description 用于数据库操作的平日加班时间字段的字段名
     * @type String
     * @default prjb
     * */
    static  String SQL_OVER_TIME_ON_WEEKDAYS = "prjb";
    /**
     * @description 用于数据库操作的月总平日加班时间字段的字段名
     * @type String
     * @default prjbxs
     * */
    static  String SQL_TOTAL_OVER_TIME_ON_WEEKDAYS = "prjbxs";
    /**
     * @description 用于数据库操作的周末出勤时间字段的字段名
     * @type String
     * @default zmcq
     * */
    static  String SQL_WORK_TIME_ON_WEEKENDS = "zmcq";
    /**
     * @description 用于数据库操作的月总周末出勤时间字段的字段名
     * @type String
     * @default zmzcxs
     * */
    static  String SQL_TOTAL_WORK_TIME_ON_WEEKENDS = "zmzcxs";
    /**
     * @description 用于数据库操作的周末加班时间字段的字段名
     * @type String
     * @default zmjb
     * */
    static  String SQL_OVER_TIME_ON_WEEKENDS = "zmjb";
    /**
     * @description 用于数据库操作的月总周末加班时间字段的字段名
     * @type String
     * @default zmjbxs
     * */
    static  String SQL_TOTAL_OVER_TIME_ON_WEEKENDS = "zmjbxs";
    /**
     * @description 用于数据库操作的节日出勤时间字段的字段名
     * @type String
     * @default jrcq
     * */
    static  String SQL_WORK_TIME_ON_HOLIDAY = "jrcq";
    /**
     * @description 用于数据库操作的月总节日出勤时间字段的字段名
     * @type String
     * @default jrzcxs
     * */
    static  String SQL_TOTAL_WORK_TIME_ON_HOLIDAY = "jrzcxs";
    /**
     * @description 用于数据库操作的节日加班时间字段的字段名
     * @type String
     * @default jrjb
     * */
    static String SQL_OVER_TIME_ON_HOLIDAY = "jrjb";
    /**
     * @description 用于数据库操作的月总节日加班时间字段的字段名
     * @type String
     * @default jrjbxs
     * */
    static String SQL_TOTAL_OVER_TIME_ON_HOLIDAY = "jrjbxs";
    /**
     * @description 用于数据库操作的夜勤天数字段的字段名
     * @type String
     * @default yq
     * */
    static String SQL_NIGHT_WORK_TIMES = "yq";
    /**
     * @description sting类型转换到calendar类型
     * @param s 要变化的日期，String类型
     * @param sdf 要变化的格式
     * @return calendar 转化完成的日期，Calendar类型
     * */
    protected Calendar StringToCalendar(String s, SimpleDateFormat sdf){
        try {
            Date date = sdf.parse(s);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @description 获取时间差值
     * @param onJob 上岗时间
     * @param offJob 离岗时间
     * @return Float.valueOf((offJob.getTime().getTime() - onJob.getTime().getTime())/ (60 * 60 * 1000) % 24) 计算后的时间的差值
     * */
    protected Float getJetLegHour(Calendar onJob,Calendar offJob){
        return Float.valueOf((offJob.getTime().getTime() - onJob.getTime().getTime())/ (60 * 60 * 1000) % 24);
    }
    /**
     * @description 获取分钟差值
     * @param onJob 上岗时间
     * @param offJob 离岗时间
     * @return Float.valueOf((offJob.getTime().getTime() - onJob.getTime().getTime())/ (60 * 1000) % 60) 计算后的分钟的差值
     * */
    protected Float getJetLegMin(Calendar onJob,Calendar offJob){
        return Float.valueOf((offJob.getTime().getTime() - onJob.getTime().getTime())/ (60 * 1000) % 60);
    }
    /**
     * @description 获取秒差值
     * @param onJob 上岗时间
     * @param offJob 离岗时间
     * @return Float.valueOf((offJob.getTime().getTime() - onJob.getTime().getTime() / 1000) % 60) 计算后的秒的差值
     * */
    protected Float getJetLegSec(Calendar onJob,Calendar offJob){
        return Float.valueOf((offJob.getTime().getTime() - onJob.getTime().getTime() / 1000) % 60);
    }
    /**
     * @description 获取当前星期周一到周五按（1-7）的顺序
     * @param onJob 上岗时间
     * @return onJob.get(Calendar.DAY_OF_WEEK)-1>0?onJob.get(Calendar.DAY_OF_WEEK)-1:7 当日的星期
     * */
    protected int getWeekInt(Calendar onJob){
        return onJob.get(Calendar.DAY_OF_WEEK)-1>0?onJob.get(Calendar.DAY_OF_WEEK)-1:7;
    }
    protected List<Date> getBasicAttendanceCalendar(Integer year, Integer month){
        List<Date> calendarList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastMonthAttendanceCalendar = Calendar.getInstance();
        Calendar currentMonthAttendanceCalendar = Calendar.getInstance();
        lastMonthAttendanceCalendar.set(year,month-2,25);
        currentMonthAttendanceCalendar.set(year,month-1,1);
        int max1 = lastMonthAttendanceCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 25; i < max1; i++) {
            lastMonthAttendanceCalendar.set(Calendar.DAY_OF_MONTH, i+1);
            Date date = lastMonthAttendanceCalendar.getTime();
            calendarList.add(date);
        }
        int max2 = currentMonthAttendanceCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for(int i=0;i<25;i++){
            currentMonthAttendanceCalendar.set(Calendar.DAY_OF_MONTH,i+1);
            Date date = currentMonthAttendanceCalendar.getTime();
            calendarList.add(date);

        }
        return calendarList;
    }
    protected Float getBasicAttendanceHour(List<Date> calendarList){
        Float basicAttendanceHour = 0f;
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        for(Date date : calendarList){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String holidayType = getHolidayType(calendar);
            int weekInt = getWeekInt(calendar);
            if(holidayType.equals("")){
                if(weekInt<6){
                    basicAttendanceHour += 8;
                }
            }else if(holidayType.equals("2")){
                basicAttendanceHour += 8;
            }
        }
        return basicAttendanceHour;
    }
    /**
     * @description 获取节日类型
     * @param year int 年
     * @param month int 月
     * @param day int 日
     * @return holidayType String
     * */
    protected String getHolidayType(int year,int month,int day){
        String holidayType="";
        try {
            Connection conn = DbUtil.getConnection();
            String selectHolidaySetSql = "select * from KQ_Holiday_Set where groupid = 501 and holidaydate = ?";
            PreparedStatement psSelectHolidaySet = conn.prepareStatement(selectHolidaySetSql);
            psSelectHolidaySet.setString(1,year+"-"+month+"-"+day);
            ResultSet holidaySetRs = psSelectHolidaySet.executeQuery();
            if(holidaySetRs.next()){
                holidayType = holidaySetRs.getString(SQL_HOLIDAY_TYPE);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return holidayType;
    }
    /**
     * @description 获取节日类型
     * @param calendar Calendar
     * @return holidayType String
     * */
    protected String getHolidayType(Calendar calendar){
        String holidayType="";
        try {
            Connection conn = DbUtil.getConnection();
            String selectHolidaySetSql = "select * from KQ_Holiday_Set where groupid = 501 and holidaydate = ?";
            PreparedStatement psSelectHolidaySet = conn.prepareStatement(selectHolidaySetSql);
            psSelectHolidaySet.setString(1, DutyLogCalculate.SDF_NO_TIME.format(calendar.getTime()));
            ResultSet holidaySetRs = psSelectHolidaySet.executeQuery();
            if(holidaySetRs.next()){
                holidayType = holidaySetRs.getString(SQL_HOLIDAY_TYPE);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return holidayType;
    }
}
