package com.CLD.dataAnonymization.dao.h2.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 该表用于存储拓展节点表
 * @Author CLD
 * @Date 2018/6/3 14:33
 **/
@Entity
@Table(name = "ExpandNodeClassify")
public class ExpandNodeClassify {

    @Id
    @GeneratedValue
    private Long id;

    private String expandName;

    private String fromName;

    private String nodeName;

    private String nodeType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpandName() {
        return expandName;
    }

    public void setExpandName(String expandName) {
        this.expandName = expandName;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
