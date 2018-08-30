package com.CLD.dataAnonymization.dao.h2.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @description：该表用于存放原型节点分类信息
 * @Author CLD
 * @Date 2018/5/21 20:43
 **/
@Entity
@Table(name="ArchetypeNodeClassify")
@Getter
@Setter
public class ArchetypeNodeClassify {

    @Id
    @GeneratedValue
    private Long id;

    private String nodeName;

    @Column(length = 1000)
    private String nodePath;

    private String nodeType;

    private String archetypeName;

    private String archetypeId;

    @Column(length = 2500)
    private String description;

}
