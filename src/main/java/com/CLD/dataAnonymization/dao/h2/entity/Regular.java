package com.CLD.dataAnonymization.dao.h2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @Description: 规则表
 * @Author CLD
 * @Date 2019/2/22 12:20
 */
@Entity
@Table(name = "Regular")
@Getter
@Setter
public class Regular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libName;

    private String area;

    private String aims;
}
