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

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getIdNum() {return idNum;}

    public void setIdNum(String idNum) {this.idNum = idNum;}

    public Integer getMonth() {return month;}

    public void setMonth(Integer month) {this.month = month;}

    public Float getTotalSalary() {return totalSalary;}

    public void setTotalSalary(Float salary) {this.totalSalary = salary;}
}
