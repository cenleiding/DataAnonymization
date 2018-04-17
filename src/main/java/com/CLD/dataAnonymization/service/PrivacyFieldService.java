package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.model.ProcessingFields;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 该类用于对隐私字段的获取，展示和修改
 * @Author CLD
 * @Date 2018/4/2 13:54
 **/
public interface PrivacyFieldService {

    /**
     * 此方法用于获取隐私字段
     * 按照模板划分
     * @return
     */
    public JSONArray getPrivaryFields() throws FileNotFoundException;

    /**
     * 此方法用于获取整理后的匿名字段
     * 按照属性21种分类
     * @return
     */
    public JSONObject getOrganizedFields() throws FileNotFoundException;

    /**
     * 此方法用于获取整理后的匿名字段
     * 按照属性21种分类
     * @return
     */
    public JSONObject getOrganizedFields(JSONArray jsonArray) throws FileNotFoundException;

    /**
     * 此方法用于获取用于处理的匿名字段
     * 按照处理要求6种分类
     * @param jsonObject 整理后的字段
     * @return
     */
    public ProcessingFields getProcessingFields(JSONObject jsonObject);

    /**
     * 此方法用于获取用于处理的匿名字段
     * 按照处理要求6种分类
     * @param jsonArray 原始字段
     * @return
     */
    public ProcessingFields getProcessingFields(JSONArray jsonArray);

    /**
     * 此方法用于获取用于处理的匿名字段
     * 按照处理要求6种分类
     * @param
     * @return
     */
    public ProcessingFields getProcessingFields() throws FileNotFoundException;

    /**
     * 此方法用于检查匿名字段是否可行
     * @return
     */
    public ArrayList<String> checkFields(JSONArray jsonArray);

    /**
     * 此方法用于检查匿名字段是否可行
     * @return
     */
    public ArrayList<String> checkFields(ProcessingFields processingFields);

    /**
     * 此方法用于更新匿名字段映射表
     * @param jsonArray
     * @return
     */
    public boolean updataFieldFile(JSONArray jsonArray) throws UnsupportedEncodingException;
}
