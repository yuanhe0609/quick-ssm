package com.company.project.entity;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/14
 */
public class EmployeeSalaryEntity {
    private String name;
    private String idNum;
    private Integer month;
    private Float totalSalary;
    private Float basicSalary;
    private Float weekdaysOverTimeSalary;
    private Float weekendsSalary;
    private Float holidaySalary;
    private Float nightWorkSalary;

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getIdNum() {return idNum;}

    public void setIdNum(String idNum) {this.idNum = idNum;}

    public Integer getMonth() {return month;}

    public void setMonth(Integer month) {this.month = month;}

    public Float getTotalSalary() {return totalSalary;}

    public void setTotalSalary(Float salary) {this.totalSalary = salary;}

    public Float getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(Float basicSalary) {
        this.basicSalary = basicSalary;
    }

    public Float getWeekdaysOverTimeSalary() {
        return weekdaysOverTimeSalary;
    }

    public void setWeekdaysOverTimeSalary(Float weekdaysOverTimeSalary) {
        this.weekdaysOverTimeSalary = weekdaysOverTimeSalary;
    }

    public Float getWeekendsSalary() {
        return weekendsSalary;
    }

    public void setWeekendsSalary(Float weekendsSalary) {
        this.weekendsSalary = weekendsSalary;
    }

    public Float getHolidaySalary() {
        return holidaySalary;
    }

    public void setHolidaySalary(Float holidaySalary) {
        this.holidaySalary = holidaySalary;
    }

    public Float getNightWorkSalary() {
        return nightWorkSalary;
    }

    public void setNightWorkSalary(Float nightWorkSalary) {
        this.nightWorkSalary = nightWorkSalary;
    }
}
