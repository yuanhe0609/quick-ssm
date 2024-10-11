package com.company.project.service.impl;

import com.company.project.entity.DutyLog;
import com.company.project.service.IDutyLogService;
import com.company.project.utils.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 实现生产人员出勤统计类
 * @author: yuanhe0609
 * @time: 2024/10/10
 */
@Service
@Slf4j
public class DutyLogServiceImpl implements IDutyLogService {

    /**
     * @description sting类型转换到calendar类型
     * @param s 要变化的日期，String类型
     * @param sdf 要变化的格式
     * @return calendar 转化完成的日期，Calendar类型
     * */
    private Calendar StringToCalendar(String s, SimpleDateFormat sdf){
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
    private Float getJetLegHour(Calendar onJob,Calendar offJob){
        return Float.valueOf((offJob.getTime().getTime() - onJob.getTime().getTime())/ (60 * 60 * 1000) % 24);
    }
    /**
     * @description 获取分钟差值
     * @param onJob 上岗时间
     * @param offJob 离岗时间
     * @return Float.valueOf((offJob.getTime().getTime() - onJob.getTime().getTime())/ (60 * 1000) % 60) 计算后的分钟的差值
     * */
    private Float getJetLegMin(Calendar onJob,Calendar offJob){
        return Float.valueOf((offJob.getTime().getTime() - onJob.getTime().getTime())/ (60 * 1000) % 60);
    }
    /**
     * @description 获取秒差值
     * @param onJob 上岗时间
     * @param offJob 离岗时间
     * @return Float.valueOf((offJob.getTime().getTime() - onJob.getTime().getTime() / 1000) % 60) 计算后的秒的差值
     * */
    private Float getJetLegSec(Calendar onJob,Calendar offJob){
        return Float.valueOf((offJob.getTime().getTime() - onJob.getTime().getTime() / 1000) % 60);
    }
    /**
     * @description 获取当前星期周一到周五按（1-7）的顺序
     * @param onJob 上岗时间
     * @return onJob.get(Calendar.DAY_OF_WEEK)-1>0?onJob.get(Calendar.DAY_OF_WEEK)-1:7 当日的星期
     * */
    private int getWeekInt(Calendar onJob){
        return onJob.get(Calendar.DAY_OF_WEEK)-1>0?onJob.get(Calendar.DAY_OF_WEEK)-1:7;
    }
    /**
     * @description 计算在岗时间
     * @param onJob 上岗时间
     * @param offJob 离岗时间
     * @return 8 or getJetLegHour(onJob, offJob)+ Float.valueOf(df.format(getJetLegMin(onJob,offJob) / 60)) 日工作时间超过8小时返回8，否则返回实际时间
     * */
    private Float getWorkTime(Calendar onJob,Calendar offJob){
        if(getJetLegHour(onJob,offJob) > 8){
            return 8F;
        }else{
            return getJetLegHour(onJob, offJob)+ Float.valueOf(DF.format(getJetLegMin(onJob,offJob) / 60));
        }
    }
    /**
     * @description 计算加班时间
     * @param onJob 上岗时间
     * @param offJob 离岗时间
     * @return 0 or getJetLegHour(onJob,offJob)-8 + Float.valueOf(df.format(getJetLegMin(onJob,offJob) / 60)) 日工作时间超过8小时，则返回超过的时间，否则返回0
     * */
    private Float getOverTime(Calendar onJob,Calendar offJob){
        if(getJetLegHour(onJob,offJob) > 8){
            return getJetLegHour(onJob,offJob)-8 + Float.valueOf(DF.format(getJetLegMin(onJob,offJob) / 60));
        }else{
            return 0F;
        }
    }
    /**
     * @description 获取节日类型
     * @param year int 年
     * @param month int 月
     * @param day int 日
     * @return holidayType String
     * */
    private String setHolidayType(int year,int month,int day){
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
     * @description 计算每日出勤时间和加班时间
     * @param dutyLogResultSet ResultSet 数据集
     * @param workType String 数据库中正常出勤时间字段名
     * @param overType String数据库中加班时间字段名
     * @return result List<Map<Integer,Float>>
     * */
    private List<Map<Integer,Float>> getWorkTimeAndOverTimeMap(ResultSet dutyLogResultSet,String workType,String overType) throws SQLException {
        List<Map<Integer,Float>> result = new ArrayList<>();
        Map<Integer,Float> dailyWorkTime = new HashMap<>();
        Map<Integer,Float> dailyOverTime = new HashMap<>();
        while(dutyLogResultSet.next()){
            Calendar nowCalendar = StringToCalendar(dutyLogResultSet.getString("modedatacreatedate"),SDF_NO_TIME);
            Integer nowaday = nowCalendar.get(Calendar.DAY_OF_MONTH);
            if(!dailyWorkTime.containsKey(nowaday) && !dailyOverTime.containsKey(nowaday)){
                dailyWorkTime.put(nowaday,dutyLogResultSet.getFloat(workType));
                dailyOverTime.put(nowaday,dutyLogResultSet.getFloat(overType));
            }else{
                Float workTime = dailyWorkTime.get(nowaday) + dutyLogResultSet.getFloat(workType);
                if(workTime >= 8){
                    dailyWorkTime.replace(nowaday,8f);
                    if(!dailyOverTime.containsKey(nowaday)){
                        dailyOverTime.put(nowaday,workTime-8);
                    }else {
                        Float OverTime = dailyOverTime.get(nowaday) + (workTime - 8);
                        dailyOverTime.replace(nowaday, OverTime);
                    }
                }else{
                    dailyWorkTime.replace(nowaday,workTime);
                }
            }
        }
        if(dutyLogResultSet.getType() == 1004){
            dutyLogResultSet.absolute(0);
        }else{
            log.warn("需要设值ResultSet.TYPE_SCROLL_INSENSITIVE和ResultSet.CONCUR_READ_ONLY");
        }
        result.add(dailyWorkTime);
        result.add(dailyOverTime);
        return result;
    }
    /**
     * @description 计算月度工作时间
     * @param workTimeMap Map<Integer,Float> 需要计算的值
     * @return totalTime Float
     * */
    private Float getTotalWorkTime(Map<Integer,Float> workTimeMap){
        Float totalTime = 0F;

        Iterator<Map.Entry<Integer,Float>> dailyWeekdaysWorkTimeIt = workTimeMap.entrySet().iterator();
        while(dailyWeekdaysWorkTimeIt.hasNext()){
            Map.Entry<Integer,Float> dailyWorkTime = dailyWeekdaysWorkTimeIt.next();
            totalTime += dailyWorkTime.getValue();
        }
        return totalTime;
    }
    /**
     * @description 更新出勤表
     * @param dutyLogResultSet ResultSet
     * @return result List<DutyLog>
     * */
    @Override
    public List<DutyLog> updateAttendanceList(ResultSet dutyLogResultSet) throws SQLException {

        List<DutyLog> result = new ArrayList<>();

        while (dutyLogResultSet.next()) {
            DutyLog dutyLog = new DutyLog();
            //员工月度计算对象
            String name = dutyLogResultSet.getString(SQL_NAME);
            String idNum = dutyLogResultSet.getString(SQL_IDNUM);
            Float weekdayWorkTime = 0F;
            Float weekdayOverTime = 0F;
            Float weekendWorkTime = 0F;
            Float weekendOverTime = 0F;
            Float festivalWorkTime = 0F;
            Float festivalOverTime = 0F;
            if (dutyLogResultSet.getString(SQL_ON_DUTY_TIME) != null && dutyLogResultSet.getString(SQL_OFF_DUTY_TIME) != null) {
                //从数据库获取上岗时间和离岗时间
                Calendar onJob = StringToCalendar(dutyLogResultSet.getString(SQL_ON_DUTY_TIME), SDF_WITH_TIME);
                Calendar offJob = StringToCalendar(dutyLogResultSet.getString(SQL_OFF_DUTY_TIME), SDF_WITH_TIME);
                //获取当日星期
                int dayOfWeekInt = getWeekInt(onJob);
                //输出
                log.info("----------------------------------"+name+"----------------------------------");
                log.info("原上班时间:" + onJob.getTime());
                log.info("原下班时间:" + offJob.getTime());
                //设置正常出勤时间(早上8：00到晚上17：00)
                int year = onJob.get(Calendar.YEAR);
                int month = onJob.get(Calendar.MONTH) + 1;
                int day = onJob.get(Calendar.DAY_OF_MONTH);
                Calendar onJobTime = Calendar.getInstance();
                onJobTime.set(year, month - 1, day, 8, 0, 0);
                Calendar offJobTime = Calendar.getInstance();
                offJobTime.set(year, month - 1, day, 17, 0, 0);
                //前后10分钟不计入
                if (Math.abs(getJetLegMin(onJobTime, onJob) + getJetLegHour(onJobTime, onJob) * 60) <= 10) {
                    onJob = onJobTime;
                }
                if (Math.abs(getJetLegMin(offJobTime, offJob) + getJetLegHour(offJobTime, offJob) * 60) <= 10) {
                    offJob = offJobTime;
                }
                //输出
                log.info("现上班时间:" + onJob.getTime());
                log.info("现下班时间:" + offJob.getTime());
                String holidayType = setHolidayType(year,month,day);
                log.info(holidayType);
                //计算工时，并持久化
                if (!holidayType.equals("")) {
                    if (holidayType.equals("1") || holidayType.equals("3")) {
                        festivalWorkTime = getWorkTime(onJob, offJob);
                        festivalOverTime = getOverTime(onJob, offJob);
                    } else if (holidayType.equals("2")) {
                        weekdayWorkTime = getWorkTime(onJob, offJob);
                        weekdayOverTime = getOverTime(onJob, offJob);
                    }
                } else {
                    if (dayOfWeekInt >= 6) {
                        weekendWorkTime = getWorkTime(onJob, offJob);
                        weekendOverTime = getOverTime(onJob, offJob);
                    } else {
                        weekdayWorkTime = getWorkTime(onJob, offJob);
                        weekdayOverTime = getOverTime(onJob, offJob);
                    }
                }
                log.info("平日工时:"+weekdayWorkTime+"平日加班时间:"+weekdayOverTime+"周末工时:"+weekendWorkTime+"周末加班时间:"+weekendOverTime+"节日工时:"+festivalWorkTime +"节日加班"+ festivalOverTime);
                log.info("----------------------------------"+name+"----------------------------------");
                dutyLog.setOnWorkTime(getWorkTime(onJob, offJob)+getOverTime(onJob, offJob));
                dutyLog.setAttendanceOnWeekdays(weekdayWorkTime);
                dutyLog.setOverTimeOnWeekdays(weekdayOverTime);
                dutyLog.setAttendanceOnWeekends(weekendWorkTime);
                dutyLog.setOverTimeOnWeekends(weekendOverTime);
                dutyLog.setAttendanceOnHoliday(festivalWorkTime);
                dutyLog.setOverTimeOnHoliday(festivalOverTime);
                result.add(dutyLog);
            }
        }
        return result;
    }
    /**
     * @param dutyLogResultSet ResultSet (因需要反复利用ResultSet,需在prepareStatement设置ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
     * @return result List<DutyLog>
     * @description 计算每月出勤,加班时间
     */
    @Override
    public List calculateMonthWorkTime(ResultSet dutyLogResultSet) throws SQLException {

        Map<Integer,Float> dailyWeekdaysWorkTimeMap = getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKDAYS,SQL_OVER_TIME_ON_WEEKDAYS).get(0);
        Map<Integer,Float> dailyWeekdaysOverTimeMap = getWorkTimeAndOverTimeMap(dutyLogResultSet,"prcq","prjb").get(1);
        Map<Integer,Float> dailyWeekendsWorkTimeMap = getWorkTimeAndOverTimeMap(dutyLogResultSet,"zmcq","zmjb").get(0);
        Map<Integer,Float> dailyWeekendsOverTimeMap = getWorkTimeAndOverTimeMap(dutyLogResultSet,"zmcq","zmjb").get(1);
        Map<Integer,Float> dailyHolidayWorkTimeMap = getWorkTimeAndOverTimeMap(dutyLogResultSet,"jrcq","jrjb").get(0);
        Map<Integer,Float> dailyHolidayOverTimeMap = getWorkTimeAndOverTimeMap(dutyLogResultSet,"jrcq","jrjb").get(1);

        Float totalWorkTimeOnWeekdays = getTotalWorkTime(dailyWeekdaysWorkTimeMap);
        Float totalOverTimeOnWeekdays = getTotalWorkTime(dailyWeekdaysOverTimeMap);
        Float totalWorkTimeOnWeekends = getTotalWorkTime(dailyWeekendsWorkTimeMap);
        Float totalOverTimeOnWeekends = getTotalWorkTime(dailyWeekendsOverTimeMap);
        Float totalWorkTimeOnHoliday = getTotalWorkTime(dailyHolidayWorkTimeMap);
        Float totalOverTimeOnHoliday = getTotalWorkTime(dailyHolidayOverTimeMap);
        Float totalWorkTime = totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends+totalWorkTimeOnHoliday;
        Float totalOverTime = totalOverTimeOnWeekdays+totalOverTimeOnWeekends+totalOverTimeOnHoliday;
        Float nightWorkTime = 0F;

        log.info("平日出勤时间(日=小时)"+dailyWeekdaysWorkTimeMap);
        log.info("平日加班时间(日=小时)"+ dailyWeekdaysOverTimeMap);
        log.info("周末出勤时间(日=小时)"+dailyWeekendsWorkTimeMap);
        log.info("周末出勤时间(日=小时)"+ dailyWeekendsOverTimeMap);
        log.info("节日出勤时间(日=小时)"+dailyHolidayWorkTimeMap);
        log.info("节日出勤时间(日=小时)"+ dailyHolidayOverTimeMap);
        log.info("totalWorkTime:"+totalWorkTime);
        log.info("totalOverTime:"+totalOverTime);
        log.info("totalWorkTimeOnWeekdays"+totalWorkTimeOnWeekdays);
        log.info("totalOverTimeOnWeekdays"+totalOverTimeOnWeekdays);
        log.info("totalWorkTimeOnWeekends"+totalWorkTimeOnWeekends);
        log.info("totalOverTimeOnWeekends"+totalOverTimeOnWeekends);
        log.info("totalWorkTimeOnHoliday"+totalWorkTimeOnHoliday);
        log.info("totalOverTimeOnHoliday"+totalOverTimeOnHoliday);

        return null;
    }



}
