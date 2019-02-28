package com.CLD.dataAnonymization.service.regularAndDictionary.testRun;

import java.util.HashMap;

/**
 * @Description: 字典和规则的试运行
 * @Author CLD
 * @Date 2019/2/28 9:39
 */
public interface TestRunService {


    /**
     * 测试单个字典
     * @param libName
     * @param fileName
     * @return
     */
    public HashMap<String,String> testSimpleDictionary(String libName,String fileName,String content);


    /**
     * 测试单个规则
     * @param area
     * @param aims
     * @param content
     * @return
     */
    public HashMap<String,String> testSimpleRegular(String area,String aims,String content);


    /**
     * 整体测试
     * @param xtzd 系统字典
     * @param xtgz 系统规则
     * @param wdzd 我的字典
     * @param wdgz 我的规则
     * @param jqxx 机器学习
     * @param libName 规则库名
     * @param content 测试内容
     * @return
     */
    public HashMap<String,String> testAll(boolean xtzd,
                                          boolean xtgz,
                                          boolean wdzd,
                                          boolean wdgz,
                                          boolean jqxx,
                                          String libName,
                                          String content);
}
