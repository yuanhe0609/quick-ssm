package com.company.project.entity;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/10
 */
public class DailyDutyLog {
    /**
     * @description 当天日期
     * @type Integer
     * */
    private Integer day;
    /**
     * @description 员工名
     * @type String
     * */
    private String name;
    /**
     * @description 班次(白班,夜班)
     * @type String
     * */
    private String workType;
    /**
     * @description 上岗时间
     * @type Float
     * */
    private Float onDutyTime;
    /**
     * @description 离岗时间
     * @type Float
     * */
    private Float onWorkTime;
    /**
     * @description 平日正常工作时间
     * @type Float
     * */
    private Float attendanceOnWeekdays;
    /**
     * @description 平日加班时间
     * @type Float
     * */
    private Float overTimeOnWeekdays;
    /**
     * @description 周末正常工作时间
     * @type Float
     * */
    private Float attendanceOnWeekends;
    /**
     * @description 周末加班时间
     * @type Float
     * */
    private Float overTimeOnWeekends;
    /**
     * @description 节日正常工作时间
     * @type Float
     * */
    private Float attendanceOnHoliday;
    /**
     * @description 节日加班时间
     * @type Float
     * */
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

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }
}
