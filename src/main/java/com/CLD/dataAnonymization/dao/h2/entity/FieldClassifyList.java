package com.CLD.dataAnonymization.dao.h2.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Set;

/**
 * @description: 字段表列表
 * @Author CLD
 * @Date 2018/8/14 15:04
 */
@Entity
@Table(name = "FieldClassifyList")
public class FieldClassifyList {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String formName;

    private String father;

    private String userName;

    @Column(length = 1000)
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    private SystemUser systemUser;

    @OneToMany(mappedBy = "fieldClassifyList")
    private Set<FieldClassify> fieldClassifySet;

    @OneToMany(mappedBy = "fieldClassifyList")
    private Set<FieldChangeLog> fieldChangeLogSet;

    public SystemUser getSystemUser() {
        return systemUser;
    }

    @JsonBackReference
    public void setSystemUser(SystemUser systemUser) {
        this.systemUser = systemUser;
    }

    public Set<FieldClassify> getFieldClassifySet() {
        return fieldClassifySet;
    }

    public void setFieldClassifySet(Set<FieldClassify> fieldClassifySet) {
        this.fieldClassifySet = fieldClassifySet;
    }

    public Set<FieldChangeLog> getFieldChangeLogSet() {
        return fieldChangeLogSet;
    }

    public void setFieldChangeLogSet(Set<FieldChangeLog> fieldChangeLogSet) {
        this.fieldChangeLogSet = fieldChangeLogSet;
    }

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

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
