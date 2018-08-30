package com.CLD.dataAnonymization.dao.h2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @description：该表用于存储数据库匿名情况
 * @Author CLD
 * @Date 2018/5/31 16:32
 **/
@Entity
@Table(name = "DbInfo")
@Getter
@Setter
public class DbInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Column(length = 2500)
    private String state;

}
