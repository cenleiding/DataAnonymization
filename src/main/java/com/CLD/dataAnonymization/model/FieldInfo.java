package com.CLD.dataAnonymization.model;

/**
 * 该model 用于存储字段信息
 * @Author CLD
 * @Date 2018/5/23 9:12
 **/
public class FieldInfo {

    private String id;

    private String fieldName;

    private String fieldType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

}
