package com.CLD.dataAnonymization.model;

/**
 * 该类用于存储原型节点信息
 * @Author CLD
 * @Date 2018/5/10 9:22
 **/
public class NodeInfo {

    private String NodePath;

    private String specialiseArchetypeId;

    private String En_name;

    private String Ch_name;

    public String getNodePath() {
        return NodePath;
    }

    public void setNodePath(String nodePath) {
        NodePath = nodePath;
    }

    public String getSpecialiseArchetypeId() {
        return specialiseArchetypeId;
    }

    public void setSpecialiseArchetypeId(String specialiseArchetypeId) {
        this.specialiseArchetypeId = specialiseArchetypeId;
    }

    public String getEn_name() {
        return En_name;
    }

    public void setEn_name(String en_name) {
        En_name = en_name;
    }

    public String getCh_name() {
        return Ch_name;
    }

    public void setCh_name(String ch_name) {
        Ch_name = ch_name;
    }
}
