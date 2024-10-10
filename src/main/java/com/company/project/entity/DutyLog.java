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
}
