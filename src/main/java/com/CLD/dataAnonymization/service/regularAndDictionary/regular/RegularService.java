package com.CLD.dataAnonymization.service.regularAndDictionary.regular;

/**
 * @Description: 规则操作
 * @Author CLD
 * @Date 2019/2/22 15:17
 */
public interface RegularService {

    /**
     * 将文本形式的规则存入数据库
     * 一般只用于系统初始化的时候
     * @return
     */
    public Boolean file2Db();
}
