package com.company.project.entity;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/12
 */
public class TotalDutyLog {
    /**
     * @description 员工姓名
     * @type String
     * */
    private String name;
    /**
     * @description 身份证号
     * @type String
     * */
    private String idNum;
    /**
     * @description 当天月份
     * @type Integer
     * */
    private Integer month;
    /**
     * @description 当月平日正常工作时间
     * @type Float
     * */
    private Float totalWorkTimeOnWeekdays;
    /**
     * @description 当月平日加班时间
     * @type Float
     * */
    private Float totalOverTimeOnWeekdays;
    /**
     * @description 当月周末正常时间
     * @type Float
     * */
    private Float totalWorkTimeOnWeekends;
    /**
     * @description 当月周末加班时间
     * @type Float
     * */
    private Float totalOverTimeOnWeekends;
    /**
     * @description 当月节日正常时间
     * @type Float
     * */
    private Float totalWorkTimeOnHoliday;
    /**
     * @description 当月节日加班时间
     * @type Float
     * */
    private Float totalOverTimeOnHoliday;
    /**
     * @description 当月总正常工作时间
     * @type Float
     * */
    private Float totalWorkTime;
    /**
     * @description 当月总加班时间
     * @type Float
     * */
    private Float totalOverTime;
    /**
     * @description 当月总夜勤次数
     * @type Integer
     * */
    private Integer nightWorkTime;

    public Float getTotalWorkTimeOnWeekdays() {
        return totalWorkTimeOnWeekdays;
    }

    public void setTotalWorkTimeOnWeekdays(Float totalWorkTimeOnWeekdays) {
        this.totalWorkTimeOnWeekdays = totalWorkTimeOnWeekdays;
    }

    public Float getTotalOverTimeOnWeekdays() {
        return totalOverTimeOnWeekdays;
    }

    public void setTotalOverTimeOnWeekdays(Float totalOverTimeOnWeekdays) {
        this.totalOverTimeOnWeekdays = totalOverTimeOnWeekdays;
    }

    public Float getTotalWorkTimeOnWeekends() {
        return totalWorkTimeOnWeekends;
    }

    public void setTotalWorkTimeOnWeekends(Float totalWorkTimeOnWeekends) {
        this.totalWorkTimeOnWeekends = totalWorkTimeOnWeekends;
    }

    public Float getTotalOverTimeOnWeekends() {
        return totalOverTimeOnWeekends;
    }

    public void setTotalOverTimeOnWeekends(Float totalOverTimeOnWeekends) {
        this.totalOverTimeOnWeekends = totalOverTimeOnWeekends;
    }

    public Float getTotalWorkTimeOnHoliday() {
        return totalWorkTimeOnHoliday;
    }

    public void setTotalWorkTimeOnHoliday(Float totalWorkTimeOnHoliday) {
        this.totalWorkTimeOnHoliday = totalWorkTimeOnHoliday;
    }

    public Float getTotalOverTimeOnHoliday() {
        return totalOverTimeOnHoliday;
    }

    public void setTotalOverTimeOnHoliday(Float totalOverTimeOnHoliday) {
        this.totalOverTimeOnHoliday = totalOverTimeOnHoliday;
    }

    public Float getTotalWorkTime() {
        return totalWorkTime;
    }

    public void setTotalWorkTime(Float totalWorkTime) {
        this.totalWorkTime = totalWorkTime;
    }

    public Float getTotalOverTime() {
        return totalOverTime;
    }

    public void setTotalOverTime(Float totalOverTime) {
        this.totalOverTime = totalOverTime;
    }

    public Integer getNightWorkTime() {
        return nightWorkTime;
    }

    public void setNightWorkTime(Integer nightWorkTime) {
        this.nightWorkTime = nightWorkTime;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }
}
