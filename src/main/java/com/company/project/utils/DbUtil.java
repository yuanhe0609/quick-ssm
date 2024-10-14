package com.company.project.utils;

import com.company.project.entity.TotalDutyLogEntity;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DbUtil {
    public static final String URL = "jdbc:mysql://localhost:3306/salary";
    public static final String USER = "root";
    public static final String PASSWORD = "123456";
    private static Connection conn = null;
    static{
        try {
            //1.加载驱动程序
            Class.forName("com.mysql.cj.jdbc.Driver");
            //2. 获得数据库连接
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection(){
        return conn;
    }
    /**
     * @description 计算每日工作时间
     * */
    private static Float calculateDailyTotalWorkTime(Map dailyWeekdaysWorkTimeMap, Map dailyWeekendsWorkTimeMap, Map dailyHolidayWorkTimeMap, Integer day){
        Float totalTime =0F;
        totalTime = (Float) dailyWeekdaysWorkTimeMap.get(day)+ (Float) dailyWeekendsWorkTimeMap.get(day) + (Float) dailyHolidayWorkTimeMap.get(day) ;
        return totalTime;
    }
    /**
     * @description 获取出勤表数据
     * */
    public static ResultSet getTotalDutyLogResultSet(String start,String end){
        Connection conn = getConnection();
        String selectSql = "select * from uf_duty_log where sgsj between ? and ?order by xm,sgsj";
        PreparedStatement psSelect = null;
        try {
            psSelect = conn.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            psSelect.setObject(1,start);
            psSelect.setObject(2,end);
            ResultSet dutyLogRs = psSelect.executeQuery();
            return dutyLogRs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @description 更新每日工作时间记录
     * */
    public static void updateDailyDutyLog(String name, Map<String,Map> mp,String sfzh,Integer month,Integer year){
        Connection conn = getConnection();
        String updateSql = "insert into uf_daily_log (xm,sfzh,rq,cqsj,jbsj) values (?,?,?,?,?)";
        String nowMonth = "";
        if(month<10){
            nowMonth = "0"+month;
        }else{
            nowMonth = ""+month;
        }
        try {
            PreparedStatement psUpdate = conn.prepareStatement(updateSql);
            Map dailyWeekdaysWorkTimeMap = mp.get("dailyWeekdaysWorkTimeMap");
            Map dailyWeekdaysOverTimeMap = mp.get("dailyWeekdaysOverTimeMap");
            Map dailyWeekendsWorkTimeMap = mp.get("dailyWeekendsWorkTimeMap");
            Map dailyWeekendsOverTimeMap = mp.get("dailyWeekendsOverTimeMap");
            Map dailyHolidayWorkTimeMap = mp.get("dailyHolidayWorkTimeMap");
            Map dailyHolidayOverTimeMap = mp.get("dailyHolidayOverTimeMap");
            System.out.println(dailyWeekdaysWorkTimeMap.get(24).getClass());
            Set keySet = dailyWeekdaysWorkTimeMap.keySet();
            for(Object i : keySet){
                if(dailyWeekdaysWorkTimeMap.containsKey(i)){
                    psUpdate.setObject(1,name);
                    psUpdate.setObject(2,sfzh);
                    if(Integer.parseInt(i.toString())<10){
                        psUpdate.setObject(3,year+"-"+month+"-0"+Integer.parseInt(i.toString()));
                    }else{
                        psUpdate.setObject(3,year+"-"+month+"-"+Integer.parseInt(i.toString()));
                    }
                    psUpdate.setObject(4,calculateDailyTotalWorkTime(dailyWeekdaysWorkTimeMap,dailyWeekendsWorkTimeMap,dailyHolidayWorkTimeMap,Integer.parseInt(i.toString())));
                    psUpdate.setObject(5,calculateDailyTotalWorkTime(dailyWeekdaysOverTimeMap,dailyWeekendsOverTimeMap,dailyHolidayOverTimeMap,Integer.parseInt(i.toString())));
                    psUpdate.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @description 更新每月工作时间记录
     * */
    public static void updateMonthDutyLog(List<TotalDutyLogEntity> totalDutyLogEntityList, String month){
        Connection conn = getConnection();
        String updateSql = "insert into uf_monthly_log (xm,sfzh,yf,zcqxss,zcts,yq,prcqxs,prjbxs,zmzcxs,zmjbxs,jrzcxs,jrjbxs) select ?,?,?,?,?,?,?,?,?,?,?,? from DUAL where not exists(select xm,yf from uf_monthly_log where xm =? and yf = ?)";
        try {
            for(TotalDutyLogEntity totalDutyLogEntity : totalDutyLogEntityList){
                PreparedStatement psUpdate = conn.prepareStatement(updateSql);
                psUpdate.setObject(1, totalDutyLogEntity.getName());
                psUpdate.setObject(2, totalDutyLogEntity.getIdNum());
                psUpdate.setObject(3,month);
                psUpdate.setObject(4, totalDutyLogEntity.getTotalOverTime()+ totalDutyLogEntity.getTotalWorkTime());
                psUpdate.setObject(5,(totalDutyLogEntity.getTotalOverTime()+ totalDutyLogEntity.getTotalWorkTime())/8);
                psUpdate.setObject(6, totalDutyLogEntity.getNightWorkTime());
                psUpdate.setObject(7, totalDutyLogEntity.getTotalWorkTimeOnWeekdays());
                psUpdate.setObject(8, totalDutyLogEntity.getTotalOverTimeOnWeekdays());
                psUpdate.setObject(9, totalDutyLogEntity.getTotalWorkTimeOnWeekends());
                psUpdate.setObject(10, totalDutyLogEntity.getTotalOverTimeOnWeekends());
                psUpdate.setObject(11, totalDutyLogEntity.getTotalWorkTimeOnHoliday());
                psUpdate.setObject(12, totalDutyLogEntity.getTotalOverTimeOnHoliday());
                psUpdate.setObject(13, totalDutyLogEntity.getName());
                psUpdate.setObject(14,month);
                psUpdate.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @description 获取出勤表数据
     * */
    public static ResultSet getMonthlyDutyLogResultSet(String date){
        Connection conn = getConnection();
        String selectSql = "select * from uf_monthly_log where yf = ? order by xm";
        PreparedStatement psSelect = null;
        try {
            psSelect = conn.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            psSelect.setObject(1,date);
            ResultSet dutyLogRs = psSelect.executeQuery();
            return dutyLogRs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

