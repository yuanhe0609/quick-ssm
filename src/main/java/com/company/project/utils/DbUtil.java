package com.company.project.utils;

import java.sql.*;
import java.util.Iterator;
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

    public static ResultSet getTotalDutyLogResultSet(String date){
        Connection conn = getConnection();
        String selectSql = "select * from uf_duty_log where sgsj like ? order by xm,sgsj";
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

    public static ResultSet getDutyLogResultSet(String name,String date){
        Connection conn = getConnection();
        String selectSql = "select * from uf_duty_log where sgsj like ? and xm = ? order by xm,sgsj";
        PreparedStatement psSelect = null;
        try {
            psSelect = conn.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            psSelect.setObject(1,date);
            psSelect.setObject(2,name);
            ResultSet dutyLogRs = psSelect.executeQuery();
            return dutyLogRs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateDutyLog(String name, Map<String,Map> mp,String sfzh,Integer month,Integer year){
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
    private static Float calculateDailyTotalWorkTime(Map dailyWeekdaysWorkTimeMap, Map dailyWeekendsWorkTimeMap, Map dailyHolidayWorkTimeMap, Integer day){
        Float totalTime =0F;
        totalTime = (Float) dailyWeekdaysWorkTimeMap.get(day)+ (Float) dailyWeekendsWorkTimeMap.get(day) + (Float) dailyHolidayWorkTimeMap.get(day) ;
        return totalTime;
    }
}

