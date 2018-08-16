package com.CLD.dataAnonymization.dao.h2.entity;

import javax.persistence.*;
import java.sql.Date;

/**
 * @description: 改动记录
 * @Author CLD
 * @Date 2018/8/14 15:08
 */
@Entity
@Table(name = "FieldChangeLog")
public class FieldChangeLog {

    @Id
    @GeneratedValue
    private Long id;

    private String formName;

    private Date dateTime;

    @Column(length = 8000)
    private String changeLog;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
