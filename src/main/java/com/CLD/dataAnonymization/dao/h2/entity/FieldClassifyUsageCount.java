package com.CLD.dataAnonymization.dao.h2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @description: 字段表使用情况
 * @Author CLD
 * @Date 2018/8/16 14:29
 */
@Entity
@Table(name="FieldClassifyUsageCount")
@Getter
@Setter
public class FieldClassifyUsageCount{

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String formName;

    private int count;

}
