package com.CLD.dataAnonymization.dao.h2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @description：该表用于存储拓展节点表
 * @Author CLD
 * @Date 2018/6/3 14:33
 **/
@Entity
@Table(name = "ExpandNodeClassify")
@Getter
@Setter
public class ExpandNodeClassify {

    @Id
    @GeneratedValue
    private Long id;

    private String expandName;

    private String fromName;

    private String CH_name;

    private String EN_name;

    private String description;

    private String nodeType;

}
