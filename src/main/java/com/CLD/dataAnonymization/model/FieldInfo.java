package com.CLD.dataAnonymization.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @description:该model 用于存储字段信息
 * @Author CLD
 * @Date 2018/5/23 9:12
 **/
@Getter
@Setter
public class FieldInfo {

    private String id;

    private String fieldName;

    private String fieldType;

}
