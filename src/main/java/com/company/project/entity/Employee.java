package com.company.project.entity;

import java.util.Date;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/10
 */
public class Employee {
    private String name;
    private String idNum;
    private Date onDutyTime;
    private Date offDutyTime;


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

    public Date getOnDutyTime() {
        return onDutyTime;
    }

    public void setOnDutyTime(Date onDutyTime) {
        this.onDutyTime = onDutyTime;
    }

    public Date getOffDutyTime() {
        return offDutyTime;
    }

    public void setOffDutyTime(Date offDutyTime) {
        this.offDutyTime = offDutyTime;
    }
}
