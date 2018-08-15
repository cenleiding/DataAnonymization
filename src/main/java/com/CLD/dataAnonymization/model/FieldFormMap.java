package com.CLD.dataAnonymization.model;

import java.util.Map;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/15 15:14
 */
public class FieldFormMap {
    private String userName;

    private Map<String,String> formNameAndDes;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, String> getFormNameAndDes() {
        return formNameAndDes;
    }

    public void setFormNameAndDes(Map<String, String> formNameAndDes) {
        this.formNameAndDes = formNameAndDes;
    }
}
