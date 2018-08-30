package com.CLD.dataAnonymization.dao.h2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @description: 字段表列表
 * @Author CLD
 * @Date 2018/8/14 15:04
 */
@Entity
@Table(name = "FieldClassifyList")
@Getter
@Setter
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

}
