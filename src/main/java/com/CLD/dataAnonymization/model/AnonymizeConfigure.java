package com.CLD.dataAnonymization.model;

/**
 * 匿名化的配置信息
 * @description:
 * @Author CLD
 * @Date 2018/8/23 14:55
 */
public class AnonymizeConfigure {

    private String                 fieldFormName;

    private String                 level;

    private String                 encryptPassword;

    private String                 noiseScope_big;

    private String                 noiseScope_small;

    private String                 k_big;

    private String                 k_small;

    private String                 t;

    private String                 suppressionLimit_level1;

    private String                 suppressionLimit_level2;

    private String                 microaggregation;

    public String getFieldFormName() {
        return fieldFormName;
    }

    public void setFieldFormName(String fieldFormName) {
        this.fieldFormName = fieldFormName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getEncryptPassword() {
        return encryptPassword;
    }

    public void setEncryptPassword(String encryptPassword) {
        this.encryptPassword = encryptPassword;
    }

    public String getNoiseScope_big() {
        return noiseScope_big;
    }

    public void setNoiseScope_big(String noiseScope_big) {
        this.noiseScope_big = noiseScope_big;
    }

    public String getNoiseScope_small() {
        return noiseScope_small;
    }

    public void setNoiseScope_small(String noiseScope_small) {
        this.noiseScope_small = noiseScope_small;
    }

    public String getK_big() {
        return k_big;
    }

    public void setK_big(String k_big) {
        this.k_big = k_big;
    }

    public String getK_small() {
        return k_small;
    }

    public void setK_small(String k_small) {
        this.k_small = k_small;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getSuppressionLimit_level1() {
        return suppressionLimit_level1;
    }

    public void setSuppressionLimit_level1(String suppressionLimit_level1) {
        this.suppressionLimit_level1 = suppressionLimit_level1;
    }

    public String getSuppressionLimit_level2() {
        return suppressionLimit_level2;
    }

    public void setSuppressionLimit_level2(String suppressionLimit_level2) {
        this.suppressionLimit_level2 = suppressionLimit_level2;
    }

    public String getMicroaggregation() {
        return microaggregation;
    }

    public void setMicroaggregation(String microaggregation) {
        this.microaggregation = microaggregation;
    }
}
