package com.company.project.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.company.project.entity.EmployeeSalaryEntity;
import com.company.project.entity.TotalDutyLogEntity;
import com.company.project.service.EmployeeSalaryCalculate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/11
 */
@Service
@Slf4j
public class EmployeeSalaryCalculateImpl extends BaseCalculate implements EmployeeSalaryCalculate {
    /**
     * @param totalDutyLogResultSet ResultSet
     * @param year Integer
     * @param month Integer
     * @return result List<EmployeeSalaryEntity>
     * @description 计算所有人每月工资
     */
    @Override
    public List<EmployeeSalaryEntity> calculateEmployeeSalary(ResultSet totalDutyLogResultSet,Integer year,Integer month) throws SQLException {
        List<EmployeeSalaryEntity> result = new ArrayList<>();
        Float basicTotalWorkTime = getBasicAttendanceHour(getBasicAttendanceCalendar(year,month));
        while(totalDutyLogResultSet.next()){
            EmployeeSalaryEntity employeeSalary = new EmployeeSalaryEntity();
            Float totalWorkTimeOnWeekdays = totalDutyLogResultSet.getFloat(SQL_TOTAL_WORK_TIME_ON_WEEKDAYS);
            Float totalWorkTimeOnWeekends = totalDutyLogResultSet.getFloat(SQL_TOTAL_WORK_TIME_ON_WEEKENDS)+totalDutyLogResultSet.getFloat(SQL_TOTAL_OVER_TIME_ON_WEEKENDS);
            Float totalWorkTimeOnHoliday = totalDutyLogResultSet.getFloat(SQL_TOTAL_WORK_TIME_ON_HOLIDAY)+totalDutyLogResultSet.getFloat(SQL_TOTAL_OVER_TIME_ON_HOLIDAY);
            if(totalWorkTimeOnWeekdays<basicTotalWorkTime){
                if(totalWorkTimeOnWeekends> 0){
                    if(totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends > basicTotalWorkTime){
                        totalWorkTimeOnWeekdays = basicTotalWorkTime;
                        totalWorkTimeOnWeekends = totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends-basicTotalWorkTime;
                    }else{
                        totalWorkTimeOnWeekdays = totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends;
                        totalWorkTimeOnWeekends = 0F;
                    }
                }
            }
            employeeSalary.setName(totalDutyLogResultSet.getString(SQL_NAME));
            employeeSalary.setIdNum(totalDutyLogResultSet.getString(SQL_IDNUM));
            employeeSalary.setMonth(month);
            employeeSalary.setSalary((float) (BASIC_MONTHLY_SALARY*totalWorkTimeOnWeekdays/basicTotalWorkTime+BASIC_MONTHLY_SALARY/21.75/8*1.5*totalDutyLogResultSet.getFloat(SQL_TOTAL_OVER_TIME_ON_WEEKDAYS)+BASIC_MONTHLY_SALARY/21.75/8*2*totalWorkTimeOnWeekends+BASIC_MONTHLY_SALARY/21.75/8*3*totalWorkTimeOnHoliday+totalDutyLogResultSet.getInt(SQL_NIGHT_WORK_TIMES)*50));
            log.info("{name:"+ employeeSalary.getName()+",salary:"+employeeSalary.getSalary()+",base:"+BASIC_MONTHLY_SALARY*totalWorkTimeOnWeekdays/basicTotalWorkTime+",add:"+BASIC_MONTHLY_SALARY/21.75/8*1.5*totalDutyLogResultSet.getFloat(SQL_TOTAL_OVER_TIME_ON_WEEKDAYS)+",weekends:"+BASIC_MONTHLY_SALARY/21.75/8*2*totalWorkTimeOnWeekends+",holiday:"+BASIC_MONTHLY_SALARY/21.75/8*3*totalWorkTimeOnHoliday+",night:"+totalDutyLogResultSet.getInt(SQL_NIGHT_WORK_TIMES)*50+"}");
            result.add(employeeSalary);
        }
        return result;
    }
    /**
     * @param totalDutyLogResultSet ResultSet
     * @return result List<EmployeeSalaryEntity>
     * @description 计算所有人每月工资
     */
    @Override
    public List<EmployeeSalaryEntity> calculateEmployeeSalary(ResultSet totalDutyLogResultSet) throws SQLException {
        List<EmployeeSalaryEntity> result = new ArrayList<>();
        while(totalDutyLogResultSet.next()){
            Integer year = Integer.valueOf(totalDutyLogResultSet.getString(SQL_MONTH).split("-")[0]);
            Integer month = Integer.valueOf(totalDutyLogResultSet.getString(SQL_MONTH).split("-")[1]);
            Float basicTotalWorkTime = getBasicAttendanceHour(getBasicAttendanceCalendar(year,month));
            EmployeeSalaryEntity employeeSalary = new EmployeeSalaryEntity();
            Float totalWorkTimeOnWeekdays = totalDutyLogResultSet.getFloat(SQL_TOTAL_WORK_TIME_ON_WEEKDAYS);
            Float totalWorkTimeOnWeekends = totalDutyLogResultSet.getFloat(SQL_TOTAL_WORK_TIME_ON_WEEKENDS)+totalDutyLogResultSet.getFloat(SQL_TOTAL_OVER_TIME_ON_WEEKENDS);
            Float totalWorkTimeOnHoliday = totalDutyLogResultSet.getFloat(SQL_TOTAL_WORK_TIME_ON_HOLIDAY)+totalDutyLogResultSet.getFloat(SQL_TOTAL_OVER_TIME_ON_HOLIDAY);
            if(totalWorkTimeOnWeekdays<basicTotalWorkTime){
                if(totalWorkTimeOnWeekends> 0){
                    if(totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends > basicTotalWorkTime){
                        totalWorkTimeOnWeekdays = basicTotalWorkTime;
                        totalWorkTimeOnWeekends = totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends-basicTotalWorkTime;
                    }else{
                        totalWorkTimeOnWeekdays = totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends;
                        totalWorkTimeOnWeekends = 0F;
                    }
                }
            }
            employeeSalary.setName(totalDutyLogResultSet.getString(SQL_NAME));
            employeeSalary.setIdNum(totalDutyLogResultSet.getString(SQL_IDNUM));
            employeeSalary.setMonth(month);
            employeeSalary.setSalary((float) (BASIC_MONTHLY_SALARY*totalWorkTimeOnWeekdays/basicTotalWorkTime+BASIC_MONTHLY_SALARY/21.75/8*1.5*totalDutyLogResultSet.getFloat(SQL_TOTAL_OVER_TIME_ON_WEEKDAYS)+BASIC_MONTHLY_SALARY/21.75/8*2*totalWorkTimeOnWeekends+BASIC_MONTHLY_SALARY/21.75/8*3*totalWorkTimeOnHoliday+totalDutyLogResultSet.getInt(SQL_NIGHT_WORK_TIMES)*50));
            log.info("{name:"+ employeeSalary.getName()+",salary:"+employeeSalary.getSalary()+",base:"+BASIC_MONTHLY_SALARY*totalWorkTimeOnWeekdays/basicTotalWorkTime+",add:"+BASIC_MONTHLY_SALARY/21.75/8*1.5*totalDutyLogResultSet.getFloat(SQL_TOTAL_OVER_TIME_ON_WEEKDAYS)+",weekends:"+BASIC_MONTHLY_SALARY/21.75/8*2*totalWorkTimeOnWeekends+",holiday:"+BASIC_MONTHLY_SALARY/21.75/8*3*totalWorkTimeOnHoliday+",night:"+totalDutyLogResultSet.getInt(SQL_NIGHT_WORK_TIMES)*50+"}");
            result.add(employeeSalary);
        }
        return result;
    }
    /**
     * @param totalDutyLogEntityList List<TotalDutyLogEntity>
     * @param year Integer
     * @param month Integer
     * @return result List<EmployeeSalaryEntity>
     * @description 计算所有人每月工资
     */
    @Override
    public List<EmployeeSalaryEntity> calculateEmployeeSalary(List<TotalDutyLogEntity> totalDutyLogEntityList,Integer year,Integer month) throws SQLException {
        List<EmployeeSalaryEntity> result = new ArrayList<>();
        Float basicTotalWorkTime = getBasicAttendanceHour(getBasicAttendanceCalendar(year,month));
        for(TotalDutyLogEntity totalDutyLog : totalDutyLogEntityList){
            EmployeeSalaryEntity employeeSalary = new EmployeeSalaryEntity();
            Float totalWorkTimeOnWeekdays = totalDutyLog.getTotalWorkTimeOnWeekdays();
            Float totalWorkTimeOnWeekends = totalDutyLog.getTotalWorkTimeOnWeekends()+totalDutyLog.getTotalOverTimeOnWeekends();
            Float totalWorkTimeOnHoliday = totalDutyLog.getTotalWorkTimeOnHoliday()+totalDutyLog.getTotalOverTimeOnHoliday();
            if(totalWorkTimeOnWeekdays<basicTotalWorkTime){
                if(totalWorkTimeOnWeekends> 0){
                    if(totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends > basicTotalWorkTime){
                        totalWorkTimeOnWeekdays = basicTotalWorkTime;
                        totalWorkTimeOnWeekends = totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends-basicTotalWorkTime;
                    }else{
                        totalWorkTimeOnWeekdays = totalWorkTimeOnWeekdays+totalWorkTimeOnWeekends;
                        totalWorkTimeOnWeekends = 0F;
                    }
                }
            }
            employeeSalary.setName(totalDutyLog.getName());
            employeeSalary.setIdNum(totalDutyLog.getIdNum());
            employeeSalary.setMonth(month);
            employeeSalary.setSalary((float) (BASIC_MONTHLY_SALARY*totalWorkTimeOnWeekdays/basicTotalWorkTime+BASIC_MONTHLY_SALARY/21.75/8*1.5*totalDutyLog.getTotalOverTimeOnWeekdays()+BASIC_MONTHLY_SALARY/21.75/8*2*totalWorkTimeOnWeekends+BASIC_MONTHLY_SALARY/21.75/8*3*totalWorkTimeOnHoliday+totalDutyLog.getNightWorkTime()*50));
            log.info("{name:"+ employeeSalary.getName()+",salary:"+employeeSalary.getSalary()+",base:"+BASIC_MONTHLY_SALARY*totalWorkTimeOnWeekdays/basicTotalWorkTime+",add:"+BASIC_MONTHLY_SALARY/21.75/8*1.5*totalDutyLog.getTotalOverTimeOnWeekdays()+",weekends:"+BASIC_MONTHLY_SALARY/21.75/8*2*totalWorkTimeOnWeekends+",holiday:"+BASIC_MONTHLY_SALARY/21.75/8*3*totalWorkTimeOnHoliday+",night:"+totalDutyLog.getNightWorkTime()*50+"}");
            result.add(employeeSalary);
        }
        return result;
    }
}
