package com.CLD.dataAnonymization.model;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/15 15:14
 */
public class FieldFormInfo {
    private String userName;

    private String formName;

    private String createTime;

    private String lastChangeTime;

    private String father;

    private String description;

    private String usageCount;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(String lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(String usageCount) {
        this.usageCount = usageCount;
    }
}
