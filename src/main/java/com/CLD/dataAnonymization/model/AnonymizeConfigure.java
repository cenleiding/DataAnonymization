package com.CLD.dataAnonymization.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @description:匿名化的配置信息
 * @Author CLD
 * @Date 2018/8/23 14:55
 */
@Getter
@Setter
public class AnonymizeConfigure {

    private String                 fieldFormName;

    private String                 level;

    private String                 regularLib;

    private String                 encryptPassword;

    private String                 noiseScope_big;

    private String                 noiseScope_small;

    private String                 k_big;

    private String                 k_small;

    private String                 t;

    private String                 suppressionLimit_level1;

    private String                 suppressionLimit_level2;

    private String                 microaggregation;
}
