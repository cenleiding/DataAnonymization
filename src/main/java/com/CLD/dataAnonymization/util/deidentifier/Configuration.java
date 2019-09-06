package com.CLD.dataAnonymization.util.deidentifier;

/**
 * @description:该类用于对匿名参数进行设置
 * @Author CLD
 * @Date 2018/8/6 15:48
 */
public class Configuration {

     public enum AnonymousLevel{
         Level1,Level2
     }

     private AnonymousLevel         level                    =      AnonymousLevel.Level1;

     private String                 encryptPassword          =      "COM.CLD";

     private Double                 noiseScope_big           =      0.08d;

     private Double                 noiseScope_small         =      0.05d;

     private Integer                k_big                    =      5;

     private Integer                k_small                  =      3;

     private Double                 t                        =      0.2d;

     private Double                 suppressionLimit_level1  =      0.9d;

     private Double                 suppressionLimit_level2  =      0.9d;

     private Integer                microaggregation         =      10;

     private Boolean                transposed               =      false;

     private Integer                ner_corePoolSize         =      2;

     private Integer                ner_maximumPoolSize      =      4;

     private Integer                ner_batchSize            =      100;

     //已抛弃
     private String                 ner_url                  =      "http://172.16.119.212:8080/v1/models/EMR_NER:predict";

    public Integer getNer_corePoolSize() {
        return ner_corePoolSize;
    }

    public void setNer_corePoolSize(Integer ner_corePoolSize) {
        this.ner_corePoolSize = ner_corePoolSize;
    }

    public Integer getNer_maximumPoolSize() {
        return ner_maximumPoolSize;
    }

    public void setNer_maximumPoolSize(Integer ner_maximumPoolSize) {
        this.ner_maximumPoolSize = ner_maximumPoolSize;
    }

    public Integer getNer_batchSize() {
        return ner_batchSize;
    }

    public void setNer_batchSize(Integer ner_batchSize) {
        this.ner_batchSize = ner_batchSize;
    }

    public Boolean getTransposed() {
        return transposed;
    }

    public void setTransposed(Boolean transposed) {
        this.transposed = transposed;
    }

    public AnonymousLevel getLevel() {
        return level;
    }

    public void setLevel(AnonymousLevel level) {
        this.level = level;
    }

    public String getEncryptPassword() {
        return encryptPassword;
    }

    public void setEncryptPassword(String encryptPassword) {
        this.encryptPassword = encryptPassword;
    }

    public Double getNoiseScope_big() {
        return noiseScope_big;
    }

    public void setNoiseScope_big(Double noiseScope_big) {
        this.noiseScope_big = noiseScope_big;
    }

    public Double getNoiseScope_small() {
        return noiseScope_small;
    }

    public void setNoiseScope_small(Double noiseScope_small) {
        this.noiseScope_small = noiseScope_small;
    }

    public Integer getK_big() {
        return k_big;
    }

    public void setK_big(Integer k_big) {
        this.k_big = k_big;
    }

    public Integer getK_small() {
        return k_small;
    }

    public void setK_small(Integer k_small) {
        this.k_small = k_small;
    }

    public Double getT() {
        return t;
    }

    public void setT(Double t) {
        this.t = t;
    }

    public Double getSuppressionLimit_level1() {
        return suppressionLimit_level1;
    }

    public void setSuppressionLimit_level1(Double suppressionLimit_level1) {
        this.suppressionLimit_level1 = suppressionLimit_level1;
    }

    public Double getSuppressionLimit_level2() {
        return suppressionLimit_level2;
    }

    public void setSuppressionLimit_level2(Double suppressionLimit_level2) {
        this.suppressionLimit_level2 = suppressionLimit_level2;
    }

    public Integer getMicroaggregation() {
        return microaggregation;
    }

    public void setMicroaggregation(Integer microaggregation) {
        this.microaggregation = microaggregation;
    }

    public String getNer_url() {
        return ner_url;
    }

    public void setNer_url(String ner_url) {
        this.ner_url = ner_url;
    }
}
