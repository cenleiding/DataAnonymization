package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.util.deidentifier.FieldHandleOld;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;

/**
 * 该类用于对隐私字段的获取，展示和修改
 * @Author CLD
 * @Date 2018/4/2 14:00
 **/
@Service
public class PrivacyFieldServiceOldImpl implements PrivacyFieldServiceOld {

    @Override
    public JSONArray getPrivaryFields() throws FileNotFoundException, UnsupportedEncodingException {
        return FieldHandleOld.readFormMappingOriginal();
    }

    @Override
    public JSONObject getOrganizedFields() throws FileNotFoundException, UnsupportedEncodingException {
        return FieldHandleOld.readFormMappingClassified();
    }

    @Override
    public JSONObject getProcessingFields() throws FileNotFoundException, UnsupportedEncodingException {
        return FieldHandleOld.readFormMappingProcessed();
    }

    @Override
    public ArrayList<String> updataFieldFile(JSONArray jsonArray) throws FileNotFoundException, UnsupportedEncodingException {
        return FieldHandleOld.updataFieldFile(jsonArray);
    }

}
