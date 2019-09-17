package com.CLD.dataAnonymization.dao.h2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @description：该表用于用于存储API使用情况
 * @Author CLD
 * @Date 2018/4/25 10:53
 **/
@Entity
@Table(name = "ApiUsage")
@Getter
@Setter
public class ApiUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ip;

    private String time;

    @Column(length = 20000)
    private String head;

    private String size;

    private String method;
}
