package com.company.project.service;

import com.company.project.entity.DailyDutyLogEntity;
import com.company.project.entity.TotalDutyLogEntity;
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
public interface DutyLogCalculate {
    /**
     * @description 更新出勤表
     * @param dutyLogResultSet ResultSet
     * @return result List<DailyDutyLog>
     * */
    public List<DailyDutyLogEntity> calculateAttendanceList(ResultSet dutyLogResultSet) throws SQLException;
    /**
     * @description 计算个人每日出勤,加班时间
     * @param dutyLogResultSet ResultSet (因需要反复利用ResultSet,需在prepareStatement设置ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
     * @return result List<Map>
     */
    public Map<String,Map> calculateDailyWorkTime(ResultSet dutyLogResultSet) throws SQLException;
    /**
     * @description 计算所有人每日出勤,加班时间
     * @param dutyLogMap Map<String,List<DailyDutyLog>>
     * @return result List<Map<String,Map>>
     */
    public List<Map<String,Map>> calculateDailyWorkTime(Map<String,List<DailyDutyLogEntity>> dutyLogMap);
    /**
     * @description 计算个人每月总出勤,加班,夜勤时间
     * @param dutyLogResultSet ResultSet (因需要反复利用ResultSet,需在prepareStatement设置ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
     * @return result DutyLog
     */
    public TotalDutyLogEntity calculateTotalWorkTime(ResultSet dutyLogResultSet) throws SQLException;
    /**
     * @description 计算所有人每月总出勤,加班,夜勤时间
     * @param dutyLogMap Map<String,List<DailyDutyLog>>
     * @return result List<TotalDutyLog>
     */
    public List<TotalDutyLogEntity> calculateTotalWorkTime(Map<String,List<DailyDutyLogEntity>> dutyLogMap);
}
