package com.company.project.service.impl;
import com.alibaba.fastjson2.JSON;
import com.company.project.entity.DailyDutyLog;
import com.company.project.entity.TotalDutyLog;
import com.company.project.service.DutyLogCalculateFormula;
import com.company.project.utils.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
/**
 * @description: 实现生产人员出勤统计类
 * @author: yuanhe0609
 * @time: 2024/10/10
 */
@Service
@Slf4j
public class DutyLogCalculateImpl extends BaseCalculate implements DutyLogCalculateFormula {
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
     * @description 计算个人每日出勤时间和加班时间(包括平日,周末,节日)
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
            Calendar nowCalendar = StringToCalendar(dutyLogResultSet.getString(SQL_ON_DUTY_TIME),SDF_NO_TIME);
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
     * @description 计算每日平日出勤时间和加班时间
     * @param dutyLogResultSet List<DailyDutyLog>
     * @return result List<Map<Integer,Float>>
     * */
    private List<Map<Integer,Float>> getWorkTimeAndOverTimeOnWeekdaysMap(List<DailyDutyLog> dutyLogResultSet){
        List<Map<Integer,Float>> result = new ArrayList<>();
        Map<Integer,Float> dailyWorkTime = new HashMap<>();
        Map<Integer,Float> dailyOverTime = new HashMap<>();
        for(DailyDutyLog dailyDutyLog : dutyLogResultSet){
            Integer nowaday = dailyDutyLog.getDay();
            if(!dailyWorkTime.containsKey(nowaday) && !dailyOverTime.containsKey(nowaday)){
                dailyWorkTime.put(nowaday,dailyDutyLog.getAttendanceOnWeekdays());
                dailyOverTime.put(nowaday,dailyDutyLog.getOverTimeOnWeekdays());
            }else{
                Float workTime = dailyWorkTime.get(nowaday) + dailyDutyLog.getAttendanceOnWeekdays();

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
        result.add(dailyWorkTime);
        result.add(dailyOverTime);
        return result;
    }
    /**
     * @description 计算每日周末出勤时间和加班时间
     * @param dutyLogResultSet List<DailyDutyLog>
     * @return result List<Map<Integer,Float>>
     * */
    private List<Map<Integer,Float>> getWorkTimeAndOverTimeOnWeekendsMap(List<DailyDutyLog> dutyLogResultSet){
        List<Map<Integer,Float>> result = new ArrayList<>();
        Map<Integer,Float> dailyWorkTime = new HashMap<>();
        Map<Integer,Float> dailyOverTime = new HashMap<>();
        for(DailyDutyLog dailyDutyLog : dutyLogResultSet){
            Integer nowaday = dailyDutyLog.getDay();
            if(!dailyWorkTime.containsKey(nowaday) && !dailyOverTime.containsKey(nowaday)){
                dailyWorkTime.put(nowaday,dailyDutyLog.getAttendanceOnWeekends());
                dailyOverTime.put(nowaday,dailyDutyLog.getOverTimeOnWeekends());
            }else{
                Float workTime = dailyWorkTime.get(nowaday) + dailyDutyLog.getAttendanceOnWeekends();

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
        result.add(dailyWorkTime);
        result.add(dailyOverTime);
        return result;
    }
    /**
     * @description 计算每日节日出勤时间和加班时间
     * @param dutyLogResultSet List<DailyDutyLog>
     * @return result List<Map<Integer,Float>>
     * */
    private List<Map<Integer,Float>> getWorkTimeAndOverTimeOnHolidayMap(List<DailyDutyLog> dutyLogResultSet){
        List<Map<Integer,Float>> result = new ArrayList<>();
        Map<Integer,Float> dailyWorkTime = new HashMap<>();
        Map<Integer,Float> dailyOverTime = new HashMap<>();
        for(DailyDutyLog dailyDutyLog : dutyLogResultSet){
            Integer nowaday = dailyDutyLog.getDay();
            if(!dailyWorkTime.containsKey(nowaday) && !dailyOverTime.containsKey(nowaday)){
                dailyWorkTime.put(nowaday,dailyDutyLog.getAttendanceOnHoliday());
                dailyOverTime.put(nowaday,dailyDutyLog.getOverTimeOnHoliday());
            }else{
                Float workTime = dailyWorkTime.get(nowaday) + dailyDutyLog.getAttendanceOnHoliday();

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
     * @description 计算夜勤次数
     * @param dutyLogResultSet ResultSet
     * @return nightWorkTimes Integer
     * */
    private Integer calculateNightWorkTimes(ResultSet dutyLogResultSet) throws SQLException {
        Integer nightWorkTimes = 0;
        while(dutyLogResultSet.next()){
            if(dutyLogResultSet.getString("bc").equals("夜班")){
                nightWorkTimes++;
            }
        }
        return nightWorkTimes;
    }
    /**
     * @description 计算夜勤次数
     * @param dutyLogResultSet List<DailyDutyLog>
     * @return nightWorkTimes Integer
     * */
    private Integer calculateNightWorkTimes(List<DailyDutyLog> dutyLogResultSet){
        Integer nightWorkTimes = 0;
        for(DailyDutyLog dailyDutyLog : dutyLogResultSet){
            if(dailyDutyLog.getWorkType().equals("夜班")){
                nightWorkTimes++;
            }
        }
        return nightWorkTimes;
    }
    /**
     * @description 更新出勤表
     * @param dutyLogResultSet ResultSet
     * @return result List<DailyDutyLog>
     * */
    @Override
    public List<DailyDutyLog> calculateAttendanceList(ResultSet dutyLogResultSet) throws SQLException {
        List<DailyDutyLog> result = new ArrayList<>();
        while (dutyLogResultSet.next()) {
            DailyDutyLog dailyDutyLog = new DailyDutyLog();
            String name = dutyLogResultSet.getString(SQL_NAME);
            String idNum = dutyLogResultSet.getString(SQL_IDNUM);
            String workType = dutyLogResultSet.getString(SQL_WORK_TYPE);
            Float weekdayWorkTime = 0F;
            Float weekdayOverTime = 0F;
            Float weekendWorkTime = 0F;
            Float weekendOverTime = 0F;
            Float festivalWorkTime = 0F;
            Float festivalOverTime = 0F;
            if (dutyLogResultSet.getString(SQL_ON_DUTY_TIME) != null && dutyLogResultSet.getString(SQL_OFF_DUTY_TIME) != null) {
                Calendar onJob = StringToCalendar(dutyLogResultSet.getString(SQL_ON_DUTY_TIME), SDF_WITH_TIME);
                Calendar offJob = StringToCalendar(dutyLogResultSet.getString(SQL_OFF_DUTY_TIME), SDF_WITH_TIME);
                int dayOfWeekInt = getWeekInt(onJob);
                log.info("----------------------------------"+name+"----------------------------------");
                log.info("原上班时间:" + onJob.getTime());
                log.info("原下班时间:" + offJob.getTime());
                int year = onJob.get(Calendar.YEAR);
                int month = onJob.get(Calendar.MONTH) + 1;
                int day = onJob.get(Calendar.DAY_OF_MONTH);
                Calendar onJobTime = Calendar.getInstance();
                onJobTime.set(year, month - 1, day, 8, 0, 0);
                Calendar offJobTime = Calendar.getInstance();
                offJobTime.set(year, month - 1, day, 17, 0, 0);

                if (Math.abs(getJetLegMin(onJobTime, onJob) + getJetLegHour(onJobTime, onJob) * 60) <= 10) {
                    onJob = onJobTime;
                }
                if (Math.abs(getJetLegMin(offJobTime, offJob) + getJetLegHour(offJobTime, offJob) * 60) <= 10) {
                    offJob = offJobTime;
                }
                log.info("现上班时间:" + onJob.getTime());
                log.info("现下班时间:" + offJob.getTime());
                String holidayType = setHolidayType(year,month,day);
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
                dailyDutyLog.setDay(day);
                dailyDutyLog.setName(name);
                dailyDutyLog.setWorkType(workType);
                dailyDutyLog.setOnWorkTime(getWorkTime(onJob, offJob)+getOverTime(onJob, offJob));
                dailyDutyLog.setAttendanceOnWeekdays(weekdayWorkTime);
                dailyDutyLog.setOverTimeOnWeekdays(weekdayOverTime);
                dailyDutyLog.setAttendanceOnWeekends(weekendWorkTime);
                dailyDutyLog.setOverTimeOnWeekends(weekendOverTime);
                dailyDutyLog.setAttendanceOnHoliday(festivalWorkTime);
                dailyDutyLog.setOverTimeOnHoliday(festivalOverTime);
                result.add(dailyDutyLog);
            }
        }
        return result;
    }
    /**
     * @param dutyLogResultSet ResultSet (因需要反复利用ResultSet,需在prepareStatement设置ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
     * @return result List<Map>
     * @description 计算个人每日出勤,加班时间
     */
    @Override
    public Map<String,Map> calculateDailyWorkTime(ResultSet dutyLogResultSet) throws SQLException {
        Map<String,Map> result = new HashMap<>();
        result.put("dailyWeekdaysWorkTimeMap",getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKDAYS,SQL_OVER_TIME_ON_WEEKDAYS).get(0));
        result.put("dailyWeekdaysOverTimeMap",getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKDAYS,SQL_OVER_TIME_ON_WEEKDAYS).get(1));
        result.put("dailyWeekendsWorkTimeMap",getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKENDS,SQL_OVER_TIME_ON_WEEKENDS).get(0));
        result.put("dailyWeekendsOverTimeMap",getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKENDS,SQL_OVER_TIME_ON_WEEKENDS).get(1));
        result.put("dailyHolidayWorkTimeMap",getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_HOLIDAY,SQL_OVER_TIME_ON_HOLIDAY).get(0));
        result.put("dailyHolidayOverTimeMap",getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_HOLIDAY,SQL_OVER_TIME_ON_HOLIDAY).get(1));
        return result;
    }
    /**
     * @param dutyLogResultSet Map<String,List<DailyDutyLog>>
     * @return result List<Map<String,Map>>
     * @description 计算所有人每日出勤,加班时间
     */
    @Override
    public List<Map<String,Map>> calculateDailyWorkTime(Map<String,List<DailyDutyLog>> dutyLogResultSet){
        List<Map<String,Map>> result = new ArrayList<>();
        for(String key : dutyLogResultSet.keySet()){
            List<DailyDutyLog> dailyDutyLogList = dutyLogResultSet.get(key);
            Map<String,Map> dailyDutiLogMap = new HashMap<>();
            dailyDutiLogMap.put("dailyWeekdaysWorkTimeMap",getWorkTimeAndOverTimeOnWeekdaysMap(dailyDutyLogList).get(0));
            dailyDutiLogMap.put("dailyWeekdaysOverTimeMap",getWorkTimeAndOverTimeOnWeekdaysMap(dailyDutyLogList).get(1));
            dailyDutiLogMap.put("dailyWeekendsWorkTimeMap",getWorkTimeAndOverTimeOnWeekendsMap(dailyDutyLogList).get(0));
            dailyDutiLogMap.put("dailyWeekendsOverTimeMap",getWorkTimeAndOverTimeOnWeekendsMap(dailyDutyLogList).get(1));
            dailyDutiLogMap.put("dailyHolidayWorkTimeMap",getWorkTimeAndOverTimeOnHolidayMap(dailyDutyLogList).get(0));
            dailyDutiLogMap.put("dailyHolidayOverTimeMap",getWorkTimeAndOverTimeOnHolidayMap(dailyDutyLogList).get(1));
            result.add(dailyDutiLogMap);
        }
        return result;
    }
    /**
     * @param dutyLogResultSet ResultSet (因需要反复利用ResultSet,需在prepareStatement设置ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
     * @return result DutyLog
     * @description 计算个人每月总出勤,加班,夜勤时间
     */
    @Override
    public TotalDutyLog calculateTotalWorkTime(ResultSet dutyLogResultSet) throws SQLException {
        TotalDutyLog totalDutyLog = new TotalDutyLog();
        Float totalWorkTimeOnWeekdays = Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKDAYS,SQL_OVER_TIME_ON_WEEKDAYS).get(0))));
        Float totalOverTimeOnWeekdays = Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKDAYS,SQL_OVER_TIME_ON_WEEKDAYS).get(1))));
        Float totalWorkTimeOnWeekends =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKENDS,SQL_OVER_TIME_ON_WEEKENDS).get(0))));
        Float totalOverTimeOnWeekends =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKENDS,SQL_OVER_TIME_ON_WEEKENDS).get(1))));
        Float totalWorkTimeOnHoliday =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_HOLIDAY,SQL_OVER_TIME_ON_HOLIDAY).get(0))));
        Float totalOverTimeOnHoliday =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_HOLIDAY,SQL_OVER_TIME_ON_HOLIDAY).get(1))));
        Float totalWorkTime = totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends+totalWorkTimeOnHoliday;
        Float totalOverTime = totalOverTimeOnWeekdays+totalOverTimeOnWeekends+totalOverTimeOnHoliday;
        Integer nightWorkTime = calculateNightWorkTimes(dutyLogResultSet);
        totalDutyLog.setTotalWorkTimeOnWeekdays(totalWorkTimeOnWeekdays);
        totalDutyLog.setTotalOverTimeOnWeekdays(totalOverTimeOnWeekdays);
        totalDutyLog.setTotalWorkTimeOnWeekends(totalWorkTimeOnWeekends);
        totalDutyLog.setTotalOverTimeOnWeekends(totalOverTimeOnWeekends);
        totalDutyLog.setTotalWorkTimeOnHoliday(totalWorkTimeOnHoliday);
        totalDutyLog.setTotalOverTimeOnHoliday(totalOverTimeOnHoliday);
        totalDutyLog.setTotalWorkTime(totalWorkTime);
        totalDutyLog.setTotalOverTime(totalOverTime);
        totalDutyLog.setNightWorkTime(nightWorkTime);
        return totalDutyLog;
    }
    /**
     * @param dutyLogResultSet Map<String,List<DailyDutyLog>>
     * @return result List<TotalDutyLog>
     * @description 计算所有人每月总出勤,加班,夜勤时间
     */
    @Override
    public List<TotalDutyLog> calculateTotalWorkTime(Map<String,List<DailyDutyLog>> dutyLogResultSet){
        List<TotalDutyLog> result = new ArrayList<>();
        for(String key : dutyLogResultSet.keySet()){
            System.out.println(key);
            TotalDutyLog totalDutyLog = new TotalDutyLog();
            List<DailyDutyLog> dailyDutyLogList = dutyLogResultSet.get(key);
            Float totalWorkTimeOnWeekdays = Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnWeekdaysMap(dailyDutyLogList).get(0))));
            Float totalOverTimeOnWeekdays = Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnWeekdaysMap(dailyDutyLogList).get(1))));
            Float totalWorkTimeOnWeekends =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnWeekendsMap(dailyDutyLogList).get(0))));
            Float totalOverTimeOnWeekends =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnWeekendsMap(dailyDutyLogList).get(1))));
            Float totalWorkTimeOnHoliday =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnHolidayMap(dailyDutyLogList).get(0))));
            Float totalOverTimeOnHoliday =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnHolidayMap(dailyDutyLogList).get(1))));
            Float totalWorkTime = totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends+totalWorkTimeOnHoliday;
            Float totalOverTime = totalOverTimeOnWeekdays+totalOverTimeOnWeekends+totalOverTimeOnHoliday;
            Integer nightWorkTime = calculateNightWorkTimes(dailyDutyLogList);
            totalDutyLog.setTotalWorkTimeOnWeekdays(totalWorkTimeOnWeekdays);
            totalDutyLog.setTotalOverTimeOnWeekdays(totalOverTimeOnWeekdays);
            totalDutyLog.setTotalWorkTimeOnWeekends(totalWorkTimeOnWeekends);
            totalDutyLog.setTotalOverTimeOnWeekends(totalOverTimeOnWeekends);
            totalDutyLog.setTotalWorkTimeOnHoliday(totalWorkTimeOnHoliday);
            totalDutyLog.setTotalOverTimeOnHoliday(totalOverTimeOnHoliday);
            totalDutyLog.setTotalWorkTime(totalWorkTime);
            totalDutyLog.setTotalOverTime(totalOverTime);
            totalDutyLog.setNightWorkTime(nightWorkTime);
            System.out.println(JSON.toJSONString(totalDutyLog));
            result.add(totalDutyLog);
        }
        return result;
    }
}
