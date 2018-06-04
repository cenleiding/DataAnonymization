package com.CLD.dataAnonymization.dao.h2.entity;

import javax.persistence.*;

/**
 * 该表用于存储原型对应的基础字段表
 * @Author CLD
 * @Date 2018/6/3 14:37
 **/
@Entity
@Table(name = "ArchetypeBasisFieldClassify")
public class ArchetypeBasisFieldClassify {

    @Id
    @GeneratedValue
    private Long id;

    private String fieldName;

    private String fieldType;

    @Column(length = 1000)
    private String archetypePath;

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

    public String getArchetypePath() {
        return archetypePath;
    }

    public void setArchetypePath(String archetypePath) {
        this.archetypePath = archetypePath;
    }
}
