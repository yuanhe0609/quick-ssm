package com.company.project.service.impl;

import com.company.project.entity.DailyDutyLogEntity;
import com.company.project.entity.TotalDutyLogEntity;
import com.company.project.service.DutyLogCalculate;
import com.company.project.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

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
public class DutyLogCalculateImpl extends BaseCalculate implements DutyLogCalculate {
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
     * @param dailyDutyLogEntityList List<DailyDutyLog>
     * @return result List<Map<Integer,Float>>
     * */
    private List<Map<Integer,Float>> getWorkTimeAndOverTimeOnWeekdaysMap(List<DailyDutyLogEntity> dailyDutyLogEntityList){
        List<Map<Integer,Float>> result = new ArrayList<>();
        Map<Integer,Float> dailyWorkTime = new HashMap<>();
        Map<Integer,Float> dailyOverTime = new HashMap<>();
        for(DailyDutyLogEntity dailyDutyLogEntity : dailyDutyLogEntityList){
            Integer nowaday = dailyDutyLogEntity.getDay();
            if(!dailyWorkTime.containsKey(nowaday) && !dailyOverTime.containsKey(nowaday)){
                dailyWorkTime.put(nowaday, dailyDutyLogEntity.getAttendanceOnWeekdays());
                dailyOverTime.put(nowaday, dailyDutyLogEntity.getOverTimeOnWeekdays());
            }else{
                Float workTime = dailyWorkTime.get(nowaday) + dailyDutyLogEntity.getAttendanceOnWeekdays();

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
     * @param dailyDutyLogEntityList List<DailyDutyLog>
     * @return result List<Map<Integer,Float>>
     * */
    private List<Map<Integer,Float>> getWorkTimeAndOverTimeOnWeekendsMap(List<DailyDutyLogEntity> dailyDutyLogEntityList){
        List<Map<Integer,Float>> result = new ArrayList<>();
        Map<Integer,Float> dailyWorkTime = new HashMap<>();
        Map<Integer,Float> dailyOverTime = new HashMap<>();
        for(DailyDutyLogEntity dailyDutyLogEntity : dailyDutyLogEntityList){
            Integer nowaday = dailyDutyLogEntity.getDay();
            if(!dailyWorkTime.containsKey(nowaday) && !dailyOverTime.containsKey(nowaday)){
                dailyWorkTime.put(nowaday, dailyDutyLogEntity.getAttendanceOnWeekends());
                dailyOverTime.put(nowaday, dailyDutyLogEntity.getOverTimeOnWeekends());
            }else{
                Float workTime = dailyWorkTime.get(nowaday) + dailyDutyLogEntity.getAttendanceOnWeekends();
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
     * @param dailyDutyLogEntityList List<DailyDutyLog>
     * @return result List<Map<Integer,Float>>
     * */
    private List<Map<Integer,Float>> getWorkTimeAndOverTimeOnHolidayMap(List<DailyDutyLogEntity> dailyDutyLogEntityList){
        List<Map<Integer,Float>> result = new ArrayList<>();
        Map<Integer,Float> dailyWorkTime = new HashMap<>();
        Map<Integer,Float> dailyOverTime = new HashMap<>();
        for(DailyDutyLogEntity dailyDutyLogEntity : dailyDutyLogEntityList){
            Integer nowaday = dailyDutyLogEntity.getDay();
            if(!dailyWorkTime.containsKey(nowaday) && !dailyOverTime.containsKey(nowaday)){
                dailyWorkTime.put(nowaday, dailyDutyLogEntity.getAttendanceOnHoliday());
                dailyOverTime.put(nowaday, dailyDutyLogEntity.getOverTimeOnHoliday());
            }else{
                Float workTime = dailyWorkTime.get(nowaday) + dailyDutyLogEntity.getAttendanceOnHoliday();
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
     * @param dutyLogList List<DailyDutyLog>
     * @return nightWorkTimes Integer
     * */
    private Integer calculateNightWorkTimes(List<DailyDutyLogEntity> dutyLogList){
        Integer nightWorkTimes = 0;
        for(DailyDutyLogEntity dailyDutyLogEntity : dutyLogList){
            if(dailyDutyLogEntity.getWorkType().equals("夜班")){
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
    public List<DailyDutyLogEntity> calculateAttendanceList(ResultSet dutyLogResultSet) throws SQLException {
        List<DailyDutyLogEntity> result = new ArrayList<>();
        while (dutyLogResultSet.next()) {
            DailyDutyLogEntity dailyDutyLogEntity = new DailyDutyLogEntity();
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
                String holidayType = getHolidayType(year,month,day);
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
                dailyDutyLogEntity.setDay(day);
                dailyDutyLogEntity.setName(name);
                dailyDutyLogEntity.setIdNum(idNum);
                dailyDutyLogEntity.setWorkType(workType);
                dailyDutyLogEntity.setOnWorkTime(getWorkTime(onJob, offJob)+getOverTime(onJob, offJob));
                dailyDutyLogEntity.setAttendanceOnWeekdays(weekdayWorkTime);
                dailyDutyLogEntity.setOverTimeOnWeekdays(weekdayOverTime);
                dailyDutyLogEntity.setAttendanceOnWeekends(weekendWorkTime);
                dailyDutyLogEntity.setOverTimeOnWeekends(weekendOverTime);
                dailyDutyLogEntity.setAttendanceOnHoliday(festivalWorkTime);
                dailyDutyLogEntity.setOverTimeOnHoliday(festivalOverTime);
                result.add(dailyDutyLogEntity);
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
        Jedis jedis = RedisUtil.getJedis();
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
     * @param dutyLogMap Map<String,List<DailyDutyLog>>
     * @return result List<Map<String,Map>>
     * @description 计算所有人每日出勤,加班时间
     */
    @Override
    public List<Map<String,Map>> calculateDailyWorkTime(Map<String,List<DailyDutyLogEntity>> dutyLogMap){
        List<Map<String,Map>> result = new ArrayList<>();
        for(String key : dutyLogMap.keySet()){
            List<DailyDutyLogEntity> dailyDutyLogEntityList = dutyLogMap.get(key);
            Map<String,Map> dailyDutiLogMap = new HashMap<>();
            dailyDutiLogMap.put("dailyWeekdaysWorkTimeMap",getWorkTimeAndOverTimeOnWeekdaysMap(dailyDutyLogEntityList).get(0));
            dailyDutiLogMap.put("dailyWeekdaysOverTimeMap",getWorkTimeAndOverTimeOnWeekdaysMap(dailyDutyLogEntityList).get(1));
            dailyDutiLogMap.put("dailyWeekendsWorkTimeMap",getWorkTimeAndOverTimeOnWeekendsMap(dailyDutyLogEntityList).get(0));
            dailyDutiLogMap.put("dailyWeekendsOverTimeMap",getWorkTimeAndOverTimeOnWeekendsMap(dailyDutyLogEntityList).get(1));
            dailyDutiLogMap.put("dailyHolidayWorkTimeMap",getWorkTimeAndOverTimeOnHolidayMap(dailyDutyLogEntityList).get(0));
            dailyDutiLogMap.put("dailyHolidayOverTimeMap",getWorkTimeAndOverTimeOnHolidayMap(dailyDutyLogEntityList).get(1));
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
    public TotalDutyLogEntity calculateTotalWorkTime(ResultSet dutyLogResultSet) throws SQLException {
        TotalDutyLogEntity totalDutyLogEntity = new TotalDutyLogEntity();
        Float totalWorkTimeOnWeekdays = Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKDAYS,SQL_OVER_TIME_ON_WEEKDAYS).get(0))));
        Float totalOverTimeOnWeekdays = Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKDAYS,SQL_OVER_TIME_ON_WEEKDAYS).get(1))));
        Float totalWorkTimeOnWeekends =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKENDS,SQL_OVER_TIME_ON_WEEKENDS).get(0))));
        Float totalOverTimeOnWeekends =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_WEEKENDS,SQL_OVER_TIME_ON_WEEKENDS).get(1))));
        Float totalWorkTimeOnHoliday =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_HOLIDAY,SQL_OVER_TIME_ON_HOLIDAY).get(0))));
        Float totalOverTimeOnHoliday =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeMap(dutyLogResultSet,SQL_WORK_TIME_ON_HOLIDAY,SQL_OVER_TIME_ON_HOLIDAY).get(1))));
        Float totalWorkTime = totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends+totalWorkTimeOnHoliday;
        Float totalOverTime = totalOverTimeOnWeekdays+totalOverTimeOnWeekends+totalOverTimeOnHoliday;
        Integer nightWorkTime = calculateNightWorkTimes(dutyLogResultSet);
        totalDutyLogEntity.setTotalWorkTimeOnWeekdays(totalWorkTimeOnWeekdays);
        totalDutyLogEntity.setTotalOverTimeOnWeekdays(totalOverTimeOnWeekdays);
        totalDutyLogEntity.setTotalWorkTimeOnWeekends(totalWorkTimeOnWeekends);
        totalDutyLogEntity.setTotalOverTimeOnWeekends(totalOverTimeOnWeekends);
        totalDutyLogEntity.setTotalWorkTimeOnHoliday(totalWorkTimeOnHoliday);
        totalDutyLogEntity.setTotalOverTimeOnHoliday(totalOverTimeOnHoliday);
        totalDutyLogEntity.setTotalWorkTime(totalWorkTime);
        totalDutyLogEntity.setTotalOverTime(totalOverTime);
        totalDutyLogEntity.setNightWorkTime(nightWorkTime);
        return totalDutyLogEntity;
    }
    /**
     * @param dutyLogMap Map<String,List<DailyDutyLog>>
     * @return result List<TotalDutyLog>
     * @description 计算所有人每月总出勤,加班,夜勤时间
     */
    @Override
    public List<TotalDutyLogEntity> calculateTotalWorkTime(Map<String,List<DailyDutyLogEntity>> dutyLogMap){
        List<TotalDutyLogEntity> result = new ArrayList<>();
        for(String key : dutyLogMap.keySet()){
            TotalDutyLogEntity totalDutyLogEntity = new TotalDutyLogEntity();
            List<DailyDutyLogEntity> dailyDutyLogEntityList = dutyLogMap.get(key);
            String idnum ="";
            if(!dailyDutyLogEntityList.isEmpty()){
                idnum = dailyDutyLogEntityList.get(0).getIdNum();
            }
            Float totalWorkTimeOnWeekdays = Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnWeekdaysMap(dailyDutyLogEntityList).get(0))));
            Float totalOverTimeOnWeekdays = Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnWeekdaysMap(dailyDutyLogEntityList).get(1))));
            Float totalWorkTimeOnWeekends =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnWeekendsMap(dailyDutyLogEntityList).get(0))));
            Float totalOverTimeOnWeekends =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnWeekendsMap(dailyDutyLogEntityList).get(1))));
            Float totalWorkTimeOnHoliday =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnHolidayMap(dailyDutyLogEntityList).get(0))));
            Float totalOverTimeOnHoliday =  Float.valueOf(DF.format(getTotalWorkTime(getWorkTimeAndOverTimeOnHolidayMap(dailyDutyLogEntityList).get(1))));
            Float totalWorkTime = totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends+totalWorkTimeOnHoliday;
            Float totalOverTime = totalOverTimeOnWeekdays+totalOverTimeOnWeekends+totalOverTimeOnHoliday;
            Integer nightWorkTime = calculateNightWorkTimes(dailyDutyLogEntityList);
            totalDutyLogEntity.setName(key);
            totalDutyLogEntity.setIdNum(idnum);
            totalDutyLogEntity.setTotalWorkTimeOnWeekdays(totalWorkTimeOnWeekdays);
            totalDutyLogEntity.setTotalOverTimeOnWeekdays(totalOverTimeOnWeekdays);
            totalDutyLogEntity.setTotalWorkTimeOnWeekends(totalWorkTimeOnWeekends);
            totalDutyLogEntity.setTotalOverTimeOnWeekends(totalOverTimeOnWeekends);
            totalDutyLogEntity.setTotalWorkTimeOnHoliday(totalWorkTimeOnHoliday);
            totalDutyLogEntity.setTotalOverTimeOnHoliday(totalOverTimeOnHoliday);
            totalDutyLogEntity.setTotalWorkTime(totalWorkTime);
            totalDutyLogEntity.setTotalOverTime(totalOverTime);
            totalDutyLogEntity.setNightWorkTime(nightWorkTime);
            result.add(totalDutyLogEntity);
        }
        return result;
    }
}
