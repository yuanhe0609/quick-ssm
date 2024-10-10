package com.company.project.service.impl;

import com.company.project.service.IDutyLogService;
import com.company.project.utils.DbUtil;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Float.valueOf;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/9
 */
@Service
public class DutyLogService implements IDutyLogService {

    //定义日期格式
    private final SimpleDateFormat sdfWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat sdfNoTime = new SimpleDateFormat("yyyy-MM-dd");
    //定义浮点数小数点个数
    private final DecimalFormat df = new DecimalFormat("0.0");
    /**
     * @description 计算一线员工每日的工作时间
     * @return 无
     * */
    public void calculateWorkTime() throws SQLException, ParseException {
        //连接数据库
        Connection conn = DbUtil.getConnection();
        Date nowDate = new Date(System.currentTimeMillis());
        //姓名，身份证号，班次，上岗时间，离岗时间
        String selectDutyLogSql = "select xm,sfzh,bc,sgsj,lgsj from uf_duty_log";
        //在岗时间，工作时间，平日出勤，平日加班，周末出勤，周末加班，节日出勤，节日加班，姓名，班次，上岗时间
        String updateSql = "update uf_duty_log set zgsj=?,gzsj=?,prcq=?,prjb=?,zmcq=?,zmjb=?,jrcq=?,jrjb=? where xm=? and bc=? and sgsj=?";
        String selectHolidaySetSql = "select * from KQ_Holiday_Set where groupid = 501 and holidaydate = ?";
        PreparedStatement psSelectDutyLog = conn.prepareStatement(selectDutyLogSql);
        PreparedStatement psUpdate = conn.prepareStatement(updateSql);
        PreparedStatement psSelectHolidaySet = conn.prepareStatement(selectHolidaySetSql);
        ResultSet DutyLogRs = psSelectDutyLog.executeQuery();
        //对查询结果进行操作
        while (DutyLogRs.next()) {
            //员工月度计算对象
            String name = DutyLogRs.getString("xm");
            String idNum = DutyLogRs.getString("sfzh");
            Float weekdayWorkTime = 0f;
            Float weekdayOverTime = 0f;
            Float weekendWorkTime = 0f;
            Float weekendOverTime = 0f;
            Float festivalWorkTime = 0f;
            Float festivalOverTime = 0f;
            if(DutyLogRs.getString("sgsj") != null && DutyLogRs.getString("lgsj") != null){
                //从数据库获取上岗时间和离岗时间
                Calendar onJob = StringToCalendar(DutyLogRs.getString("sgsj"),sdfWithTime);
                Calendar offJob = StringToCalendar(DutyLogRs.getString("lgsj"),sdfWithTime);
                //获取当日星期
                int dayOfWeekInt = getWeekInt(onJob);
                //输出
                System.out.println("-------------------------------------------------------------------");
                System.out.println("原上班时间:" + onJob.getTime());
                System.out.println("原下班时间:" + offJob.getTime());
                //设置正常出勤时间(早上8：00到晚上17：00)
                int year = onJob.get(Calendar.YEAR);
                int mouth = onJob.get(Calendar.MONTH)+1;
                int day = onJob.get(Calendar.DAY_OF_MONTH);
                Calendar onJobTime = Calendar.getInstance();
                onJobTime.set(year,mouth-1,day,8,0,0);
                Calendar offJobTime = Calendar.getInstance();
                offJobTime.set(year,mouth-1,day,17,0,0);
                //前后10分钟不计入
                if(Math.abs(getJetLegMin(onJobTime,onJob) + getJetLegHour(onJobTime,onJob)*60) <= 10){
                    onJob = onJobTime;
                }
                if(Math.abs(getJetLegMin(offJobTime,offJob) + getJetLegHour(offJobTime,offJob)*60) <= 10){
                    offJob = offJobTime;
                }
                //输出
                System.out.println("现上班时间:" + onJob.getTime());
                System.out.println("现下班时间:" + offJob.getTime());
                //计算工时，并持久化
                psSelectHolidaySet.setString(1,year+"-"+mouth+"-"+day);
                ResultSet holidaySetRs = psSelectHolidaySet.executeQuery();
                if(holidaySetRs.next()){
                    if(holidaySetRs.getString("changetype").equals("1") || holidaySetRs.getString("changetype").equals("3")){
                        festivalWorkTime = getWorkTime(onJob,offJob);
                        festivalOverTime = getOverTime(onJob,offJob);
                    }else if(holidaySetRs.getString("changetype").equals("2")){
                        weekdayWorkTime = getWorkTime(onJob,offJob);
                        weekdayOverTime = getOverTime(onJob,offJob);
                    }
                }else {
                    if(dayOfWeekInt >= 6){
                        weekendWorkTime = getWorkTime(onJob,offJob);
                        weekendOverTime = getOverTime(onJob,offJob);
                    }else{
                        weekdayWorkTime = getWorkTime(onJob,offJob);
                        weekdayOverTime = getOverTime(onJob,offJob);
                    }
                }
                //输出
                System.out.println("平日工时:"+weekdayWorkTime+"平日加班时间:"+weekdayOverTime+"周末工时:"+weekendWorkTime+"周末加班时间:"+weekendOverTime+"节日工时:"+festivalWorkTime +"节日加班"+ festivalOverTime);
                System.out.println("-------------------------------------------------------------------");
                //执行更新操作
                psUpdate.setObject(1,getWorkTime(onJob,offJob)+getOverTime(onJob,offJob));
                psUpdate.setObject(2,getWorkTime(onJob,offJob)+getOverTime(onJob,offJob));
                psUpdate.setObject(5,weekendWorkTime);
                psUpdate.setObject(6,weekendOverTime);
                psUpdate.setObject(3,weekdayWorkTime);
                psUpdate.setObject(4,weekdayOverTime);
                psUpdate.setObject(7,festivalWorkTime);
                psUpdate.setObject(8,festivalOverTime);
                psUpdate.setObject(9,DutyLogRs.getString("xm"));
                psUpdate.setObject(10,DutyLogRs.getString("bc"));
                psUpdate.setObject(11,DutyLogRs.getString("sgsj"));
                psUpdate.executeUpdate();
            }
        }
    }
    /**
     * @description 计算工人月度出勤时间
     * @param date 当月月份
     * @param name 工人姓名
     * @return 无
     * */
    public void calculateMounthWorkTime(Date date,String name) throws SQLException, ParseException {
        Connection conn = DbUtil.getConnection();
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);

        String selectSql = "select * from uf_duty_log where modedatacreatedate like ? and xm = ? order by sgsj";
        String insertDayLogSql = "insert into t_day_log (xm,cqsj,jbsj,month,day) select ?,?,?,?,? from dual where not exists(select xm from t_day_log where xm = ? and month = ? and day = ?)";
        String insertMonthLogSql = "insert into t_month_log (xm,yf,zcqxss,zcts,prcqxs,prjbxs) select ?,?,?,?,?,? from dual where not exists(select xm from t_month_log where xm = ? and yf = ?)";
        PreparedStatement psSelect = conn.prepareStatement(selectSql);
        PreparedStatement psInsertDayLog = conn.prepareStatement(insertDayLogSql);
        PreparedStatement psInsertMonthLog = conn.prepareStatement(insertMonthLogSql);
        int year = calDate.get(Calendar.YEAR);
        int month = calDate.get(Calendar.MONTH)+1;
        if(month<10){
            psSelect.setString(1,year+"-0"+month+"-%");
        }else{
            psSelect.setString(1,year+"-"+month+"-%");
        }
        psSelect.setString(2,name);
        ResultSet rs = psSelect.executeQuery();
        Map<Integer,Float> zgsjMap = new HashMap<>();
        Map<Integer,Float> cqMap = new HashMap<>();
        Map<Integer,Float> jbMap = new HashMap<>();
        System.out.println(name+"月度出勤记录-------------------------------------");
        Float totalWorkTime = 0f;
        Float totalOverTime = 0f;
        while(rs.next()){
            Calendar everyCal = StringToCalendar(rs.getString("modedatacreatedate"),sdfNoTime);
            Integer everyday = everyCal.get(Calendar.DAY_OF_MONTH);
            if(!zgsjMap.containsKey(everyday)){
                zgsjMap.put(everyday,rs.getFloat("zgsj"));
            }else{
                Float zgsj = zgsjMap.get(everyday) + rs.getFloat("zgsj");
                zgsjMap.replace(everyday,zgsj);
            }
            if(!cqMap.containsKey(everyday) && !jbMap.containsKey(everyday)){
                cqMap.put(everyday,rs.getFloat("prcq")+rs.getFloat("zmcq")+rs.getFloat("jrcq"));
                jbMap.put(everyday,rs.getFloat("prjb")+rs.getFloat("zmjb")+rs.getFloat("jrjb"));
            }else{
                Float cq = cqMap.get(everyday) + rs.getFloat("prcq")+rs.getFloat("zmcq")+rs.getFloat("jrcq");
                if(cq >= 8){
                    cqMap.replace(everyday,8f);
                    if(!jbMap.containsKey(everyday)){
                        jbMap.put(everyday,cq-8);
                    }else {
                        Float jb = jbMap.get(everyday) + (cq - 8);
                        jbMap.replace(everyday, jb);
                    }
                }else{
                    cqMap.replace(everyday,cq);
                }
            }
        }
        System.out.println("在岗时间(日=小时)"+zgsjMap);
        System.out.println("出勤时间(日=小时)"+cqMap);
        System.out.println("加班时间(日=小时)"+jbMap);
        Iterator<Map.Entry<Integer,Float>> cqit = cqMap.entrySet().iterator();
        Iterator<Map.Entry<Integer,Float>> jbit = jbMap.entrySet().iterator();
        while(cqit.hasNext() || jbit.hasNext()){
            Map.Entry<Integer,Float> cq = cqit.next();
            Map.Entry<Integer,Float> jb = jbit.next();
            totalWorkTime += cq.getValue();
            totalOverTime += jb.getValue();
        }
        System.out.println("totalWorkTime:"+totalWorkTime);
        System.out.println("totalOverTime:"+totalOverTime);

        psInsertMonthLog.setObject(1,name);
        psInsertMonthLog.setObject(2,month);
        psInsertMonthLog.setObject(3,totalWorkTime+totalOverTime);
        psInsertMonthLog.setObject(4,(totalWorkTime+totalOverTime)/24);
        psInsertMonthLog.setObject(5,totalWorkTime);
        psInsertMonthLog.setObject(6,totalOverTime);
        psInsertMonthLog.setObject(7,name);
        psInsertMonthLog.setObject(8,month);
        psInsertMonthLog.executeUpdate();

    }

    /**
     * @description sting类型转换到calendar类型
     * @param s 要变化的日期，String类型
     * @param sdf 要变化的格式
     * @return calendar 转化完成的日期，Calendar类型
     * */
    private Calendar StringToCalendar(String s,SimpleDateFormat sdf){
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
            return 8.0f;
        }else{
            return getJetLegHour(onJob, offJob)+ Float.valueOf(df.format(getJetLegMin(onJob,offJob) / 60));
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
            return getJetLegHour(onJob,offJob)-8 + Float.valueOf(df.format(getJetLegMin(onJob,offJob) / 60));
        }else{
            return 0f;
        }
    }
}
