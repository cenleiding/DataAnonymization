package com.CLD.dataAnonymization.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/15 15:14
 */
@Getter
@Setter
public class FieldFormInfo {
    private String userName;

    private String formName;

    private String createTime;

    private String lastChangeTime;

    private String father;

    private String description;

    private String usageCount;

}
