package com.CLD.dataAnonymization.model;

/**
 * @Author CLD
 * @Date 2018/6/4 10:25
 **/
public class ExpandNodeInfo {

    private Long id;

    private String expandName;

    private String fromName;

    private String CH_name;

    private String EN_name;

    private String description;

    private String nodeType;

    public String getCH_name() {
        return CH_name;
    }

    public void setCH_name(String CH_name) {
        this.CH_name = CH_name;
    }

    public String getEN_name() {
        return EN_name;
    }

    public void setEN_name(String EN_name) {
        this.EN_name = EN_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
