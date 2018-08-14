package com.CLD.dataAnonymization.dao.h2.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

/**
 * @description: 该表用于存放字段名和其分类的映射关系
 * @Author CLD
 * @Date 2018/8/14 15:03
 */
@Entity
@Table(name = "FieldClassify")
public class FieldClassify {

    @Id
    @GeneratedValue
    private long ID;

    private String fieldName;

    private String fieldType;

    private String fromName;

    @ManyToOne(cascade = CascadeType.ALL)
    private FieldClassifyList fieldClassifyList;

    public FieldClassifyList getFieldClassifyList() {
        return fieldClassifyList;
    }

    @JsonBackReference
    public void setFieldClassifyList(FieldClassifyList fieldClassifyList) {
        this.fieldClassifyList = fieldClassifyList;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}
