package com.CLD.dataAnonymization.dao.h2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

/**
 * @Description: 字典
 * @Author CLD
 * @Date 2019/2/22 12:17
 */
@Entity
@Table(name = "Dictionary")
@Getter
@Setter
public class Dictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libName;

    private String fileName;

    private Date uploadTime;

    private String description;

    private String url;
}
