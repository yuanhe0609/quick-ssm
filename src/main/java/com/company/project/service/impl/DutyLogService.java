package com.company.project.service.impl;

import com.company.project.service.IDutyLogService;
import com.company.project.utils.DbUtil;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Float.valueOf;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/9
 */
@Service
public class DutyLogService implements IDutyLogService {

    //定义日期格式
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //定义浮点数小数点个数
    private final DecimalFormat df = new DecimalFormat("0.0");

    /**
    * 计算一线员工每日的工作时间
    * */
    public void calculateWorkTime() throws SQLException, ParseException {

        //连接数据库
        Connection conn = DbUtil.getConnection();
        //姓名，身份证号，班次，上岗时间，离岗时间
        String selectSql = "select xm,sfzh,bc,sgsj,lgsj from uf_duty_log";
        //在岗时间，工作时间，平日出勤，平日加班，周末出勤，周末加班，节日出勤，节日加班，姓名，班次，上岗时间
        String updateSql = "update uf_duty_log set zgsj=?,gzsj=?,prcq=?,prjb=?,zmcq=?,zmjb=?,jrcq=?,jrjb=? where xm=? and bc=? and sgsj=?";

        PreparedStatement psSelect = conn.prepareStatement(selectSql);
        PreparedStatement psUpdate = conn.prepareStatement(updateSql);
        ResultSet rs = psSelect.executeQuery();
        //对查询结果进行操作
        while (rs.next()) {
            //员工月度计算对象
            String name = rs.getString("xm");
            String idNum = rs.getString("sfzh");
            Float weekdayWorkTime = 0f;
            Float weekdayOverTime = 0f;
            Float weekendWorkTime = 0f;
            Float weekendOverTime = 0f;
            Float festivalWorkTime = 0f;
            Float festivalOverTime = 0f;

            if(rs.getString("sgsj") != null && rs.getString("lgsj") != null){
                //从数据库获取上岗时间和离岗时间
                Date onJobDate = sdf.parse(rs.getString("sgsj"));
                Date offJobDate = sdf.parse(rs.getString("lgsj"));
                //把Date类型转换成Calendar类型便于计算
                Calendar onJob = Calendar.getInstance();
                Calendar offJob = Calendar.getInstance();
                onJob.setTime(onJobDate);
                offJob.setTime(offJobDate);
                //获取当日星期
                int dayOfWeekInt = getWeekInt(onJob);

                System.out.println("-------------------------------------------------------------------");
                System.out.println("原上班时间:" + onJob.getTime());
                System.out.println("原下班时间:" + offJob.getTime());
                //设置正常出勤时间(早上8：00到晚上17：00)
                int year = onJob.get(Calendar.YEAR);
                int mounth = onJob.get(Calendar.MONTH);
                int day = onJob.get(Calendar.DAY_OF_MONTH);
                Calendar onJobTime = Calendar.getInstance();
                onJobTime.set(year,mounth,day,8,0,0);
                Calendar offJobTime = Calendar.getInstance();
                offJobTime.set(year,mounth,day,17,0,0);
                //前后10分钟不计入
                if(Math.abs(getJetLegMin(onJobTime,onJob) + getJetLegHour(onJobTime,onJob)*60) <= 10){
                    onJob = onJobTime;
                }
                if(Math.abs(getJetLegMin(offJobTime,offJob) + getJetLegHour(offJobTime,offJob)*60) <= 10){
                    offJob = offJobTime;
                }

                System.out.println("现上班时间:" + onJob.getTime());
                System.out.println("现下班时间:" + offJob.getTime());
                //计算工时，并持久化
                if(dayOfWeekInt >= 6){
                    weekendWorkTime = getWorkTime(onJob,offJob);
                    weekendOverTime = getOverTime(onJob,offJob);
                }else{
                    weekdayWorkTime = getWorkTime(onJob,offJob);
                    weekdayOverTime = getOverTime(onJob,offJob);
                }
                System.out.println("平日工时:"+weekdayWorkTime+"平日加班时间:"+weekdayOverTime+"周末工时:"+weekendWorkTime+"周末加班时间:"+weekendOverTime);
                System.out.println("-------------------------------------------------------------------");
                //执行更新操作
                psUpdate.setObject(1,getWorkTime(onJob,offJob));
                psUpdate.setObject(2,getWorkTime(onJob,offJob));
                psUpdate.setObject(5,weekendWorkTime);
                psUpdate.setObject(6,weekendOverTime);
                psUpdate.setObject(3,weekdayWorkTime);
                psUpdate.setObject(4,weekdayOverTime);
                psUpdate.setObject(7,festivalWorkTime);
                psUpdate.setObject(8,festivalOverTime);
                psUpdate.setObject(9,rs.getString("xm"));
                psUpdate.setObject(10,rs.getString("bc"));
                psUpdate.setObject(11,rs.getString("sgsj"));
                psUpdate.executeUpdate();
            }

        }
    }
    public void calculateMounthWorkTime(Date date) throws SQLException {
        Connection conn = DbUtil.getConnection();
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        int year = calDate.get(Calendar.YEAR);
        int mounth = calDate.get(Calendar.MONTH)+1;
        int day = calDate.get(Calendar.DAY_OF_MONTH);
        String selectSql = "select * from uf_duty_log where sgsj like ?";
        PreparedStatement psSelect = conn.prepareStatement(selectSql);
        psSelect.setString(1,year+"-"+mounth+"-"+day+"%");
        System.out.println(year+"-"+mounth+"-"+day+"%");
        ResultSet rs = psSelect.executeQuery();
        System.out.println(rs.next());
        while(rs.next()){
            System.out.println(rs.getString(1));
        }

    }


    //获取时间差值
    private Long getJetLegHour(Calendar onJob,Calendar offJob){
        return (offJob.getTime().getTime() - onJob.getTime().getTime())/ (60 * 60 * 1000) % 24;
    }
    //获取分钟差值
    private Long getJetLegMin(Calendar onJob,Calendar offJob){
        return (offJob.getTime().getTime() - onJob.getTime().getTime())/ (60 * 1000) % 60;
    }
    //获取秒数差值
    private Long getJetLegSec(Calendar onJob,Calendar offJob){
        return (offJob.getTime().getTime() - onJob.getTime().getTime() / 1000) % 60;
    }
    //获取当前星期周一到周五按（1-7）的顺序
    private int getWeekInt(Calendar onJob){
        return onJob.get(Calendar.DAY_OF_WEEK)-1>0?onJob.get(Calendar.DAY_OF_WEEK)-1:7;
    }
    //计算在岗时间
    private Float getWorkTime(Calendar onJob,Calendar offJob){
        return getJetLegHour(onJob, offJob) + Float.valueOf(df.format(getJetLegMin(onJob,offJob) / 60));
    }
    //计算加班时间
    private Float getOverTime(Calendar onJob,Calendar offJob){
        if(getJetLegHour(onJob,offJob) > 8){
            return getJetLegHour(onJob,offJob)-8 + Float.valueOf(df.format(getJetLegMin(onJob,offJob) / 60));
        }else{
            return 0f;
        }
    }
}
