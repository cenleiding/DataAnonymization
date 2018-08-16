package com.CLD.dataAnonymization.dao.h2.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/16 14:29
 */
@Entity
public class FieldClassifyUsageCount{

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String formName;

    private int count;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
