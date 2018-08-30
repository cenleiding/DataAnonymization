package com.CLD.dataAnonymization.dao.h2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @description: 该表用于存放字段名和其分类的映射关系
 * @Author CLD
 * @Date 2018/8/14 15:03
 */
@Entity
@Table(name = "FieldClassify")
@Getter
@Setter
public class FieldClassify {

    @Id
    @GeneratedValue
    private long ID;

    private String fieldName;

    private String fieldType;

    private String formName;

}
