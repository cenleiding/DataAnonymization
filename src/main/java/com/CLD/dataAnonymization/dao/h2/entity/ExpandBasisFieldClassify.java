package com.CLD.dataAnonymization.dao.h2.entity;

import javax.persistence.*;

/**
 * 该表用于存储拓展基础字段表
 * @Author CLD
 * @Date 2018/6/3 14:46
 **/
@Entity
@Table(name = "ExpandBasisFieldClassify")
public class ExpandBasisFieldClassify{

    @Id
    @GeneratedValue
    private Long id;

    private String fieldName;

    private String fieldType;

    @Column(length = 10000)
    private String expandFromName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getExpandFromName() {
        return expandFromName;
    }

    public void setExpandFromName(String expandFromName) {
        this.expandFromName = expandFromName;
    }
}
