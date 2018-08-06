package com.CLD.dataAnonymization.util.deidentifier;

/**
 * @description:该类根据 DataHandle 和 Configuration.运行相应算法，进行匿名处理。
 * @Author CLD
 * @Date 2018/8/6 16:34
 */
public class ARXAnonymizer {

    private DataHandle dataHandle;

    private Configuration configuration;

    public ARXAnonymizer(DataHandle dataHandle,Configuration configuration){
        this.dataHandle=dataHandle;
        this.configuration=configuration;
    }

    /**
     * 匿名化启动
     * @return
     */
    public Boolean anonymize(){

        return true;
    }


    public DataHandle getDataHandle() {
        return dataHandle;
    }

    public void setDataHandle(DataHandle dataHandle) {
        this.dataHandle = dataHandle;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
