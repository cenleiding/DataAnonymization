package com.CLD.dataAnonymization.dao.h2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

/**
 * @Description: 规则库，关联字典表与规则表
 * @Author CLD
 * @Date 2019/2/22 12:11
 */
@Entity
@Table(name = "RegularLib")
@Getter
@Setter
public class RegularLib {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String libName;

    private String user;

    private Date createTime;

    private Date changeTime;

    private String description;
}
