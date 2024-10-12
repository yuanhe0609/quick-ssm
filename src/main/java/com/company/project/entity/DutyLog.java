package com.company.project.entity;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/10
 */
public class DutyLog {
    private Float onDutyTime;
    private Float onWorkTime;
    private Float attendanceOnWeekdays;
    private Float overTimeOnWeekdays;
    private Float attendanceOnWeekends;
    private Float overTimeOnWeekends;
    private Float attendanceOnHoliday;
    private Float overTimeOnHoliday;
    private Float totalWorkTimeOnWeekdays;
    private Float totalOverTimeOnWeekdays;
    private Float totalWorkTimeOnWeekends;
    private Float totalOverTimeOnWeekends;
    private Float totalWorkTimeOnHoliday;
    private Float totalOverTimeOnHoliday;
    private Float totalWorkTime;
    private Float totalOverTime;
    private Integer nightWorkTime;

    public Float getOnWorkTime() {
        return onWorkTime;
    }

    public Float getOnDutyTime() {
        return onDutyTime;
    }

    public Float getAttendanceOnWeekdays() {
        return attendanceOnWeekdays;
    }

    public Float getOverTimeOnWeekdays() {
        return overTimeOnWeekdays;
    }

    public Float getAttendanceOnWeekends() {
        return attendanceOnWeekends;
    }

    public Float getAttendanceOnHoliday() {
        return attendanceOnHoliday;
    }

    public Float getOverTimeOnWeekends() {
        return overTimeOnWeekends;
    }

    public Float getOverTimeOnHoliday() {
        return overTimeOnHoliday;
    }

    public void setOnDutyTime(Float onDutyTime) {
        this.onDutyTime = onDutyTime;
    }

    public void setOnWorkTime(Float onWorkTime) {
        this.onWorkTime = onWorkTime;
    }

    public void setOverTimeOnWeekdays(Float overTimeOnWeekdays) {
        this.overTimeOnWeekdays = overTimeOnWeekdays;
    }

    public void setAttendanceOnWeekdays(Float attendanceOnWeekdays) {
        this.attendanceOnWeekdays = attendanceOnWeekdays;
    }

    public void setAttendanceOnWeekends(Float attendanceOnWeekends) {
        this.attendanceOnWeekends = attendanceOnWeekends;
    }

    public void setOverTimeOnWeekends(Float overTimeOnWeekends) {
        this.overTimeOnWeekends = overTimeOnWeekends;
    }

    public void setAttendanceOnHoliday(Float attendanceOnHoliday) {
        this.attendanceOnHoliday = attendanceOnHoliday;
    }

    public void setOverTimeOnHoliday(Float overTimeOnHoliday) {
        this.overTimeOnHoliday = overTimeOnHoliday;
    }

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

    public Float getTotalOverTimeOnHoliday() {
        return totalOverTimeOnHoliday;
    }

    public void setTotalOverTimeOnHoliday(Float totalOverTimeOnHoliday) {
        this.totalOverTimeOnHoliday = totalOverTimeOnHoliday;
    }

    public Float getTotalWorkTimeOnHoliday() {
        return totalWorkTimeOnHoliday;
    }

    public void setTotalWorkTimeOnHoliday(Float totalWorkTimeOnHoliday) {
        this.totalWorkTimeOnHoliday = totalWorkTimeOnHoliday;
    }

    public Float getTotalWorkTime() {
        return totalWorkTime;
    }

    public void setTotalWorkTime(Float totalWorkTime) {
        this.totalWorkTime = totalWorkTime;
    }

    public Integer getNightWorkTime() {
        return nightWorkTime;
    }

    public void setNightWorkTime(Integer nightWorkTime) {
        this.nightWorkTime = nightWorkTime;
    }

    public Float getTotalOverTime() {
        return totalOverTime;
    }

    public void setTotalOverTime(Float totalOverTime) {
        this.totalOverTime = totalOverTime;
    }
}
