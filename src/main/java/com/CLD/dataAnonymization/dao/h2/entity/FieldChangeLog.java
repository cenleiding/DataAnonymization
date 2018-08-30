package com.CLD.dataAnonymization.dao.h2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

/**
 * @description: 字段表改动记录
 * @Author CLD
 * @Date 2018/8/14 15:08
 */
@Entity
@Table(name = "FieldChangeLog")
@Getter
@Setter
public class FieldChangeLog {

    @Id
    @GeneratedValue
    private Long id;

    private String formName;

    private Date dateTime;

    @Column(length = 8000)
    private String changeLog;

    private String description;
}
