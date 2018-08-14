package com.CLD.dataAnonymization.dao.h2.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

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

    private String fromName;

    private Date dateTime;

    @Column(length = 8000)
    private String changeLog;

    @ManyToOne(cascade = CascadeType.ALL)
    private FieldClassifyList fieldClassifyList;

    public FieldClassifyList getFieldClassifyList() {
        return fieldClassifyList;
    }

    @JsonBackReference
    public void setFieldClassifyList(FieldClassifyList fieldClassifyList) {
        this.fieldClassifyList = fieldClassifyList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
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
}
