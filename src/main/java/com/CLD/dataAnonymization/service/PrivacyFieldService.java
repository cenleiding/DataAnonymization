package com.CLD.dataAnonymization.service;

import com.alibaba.fastjson.JSONArray;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;

/**
 * 该类用于对隐私字段的获取，展示和修改
 * @Author CLD
 * @Date 2018/4/2 13:54
 **/
public interface PrivacyFieldService {

    /**
     * 此方法用于获取隐私字段
     * @return
     */
    public JSONArray getPrivaryFields() throws FileNotFoundException;
}
