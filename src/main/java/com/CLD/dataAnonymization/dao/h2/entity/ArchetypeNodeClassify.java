package com.CLD.dataAnonymization.dao.h2.entity;


import javax.persistence.*;

/**
 * 该表用于存放原型节点分类信息
 * * @Author CLD
 * @Date 2018/5/21 20:43
 **/
@Entity
@Table(name="ArchetypeNodeClassify")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getArchetypeName() {
        return archetypeName;
    }

    public void setArchetypeName(String archetypeName) {
        this.archetypeName = archetypeName;
    }

    public String getArchetypeId() {
        return archetypeId;
    }

    public void setArchetypeId(String archetypeId) {
        this.archetypeId = archetypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
