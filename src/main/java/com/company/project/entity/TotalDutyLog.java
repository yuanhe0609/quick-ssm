package com.company.project.entity;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/12
 */
public class TotalDutyLog {
    private Integer month;
    private Float totalWorkTimeOnWeekdays;
    private Float totalOverTimeOnWeekdays;
    private Float totalWorkTimeOnWeekends;
    private Float totalOverTimeOnWeekends;
    private Float totalWorkTimeOnHoliday;
    private Float totalOverTimeOnHoliday;
    private Float totalWorkTime;
    private Float totalOverTime;
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
}
