package com.CLD.dataAnonymization.util.deidentifier;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 此类用于外部调用匿名方法
 * 并进行参数设置
 * @Author CLD
 * @Date 2018/4/12 10:14
 **/
public class IOConfiguration {

    /**用于外部调用安全屋方法
     * @param data
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<ArrayList<String>> ToSafeHarbor(ArrayList<ArrayList<String>> data) throws FileNotFoundException, UnsupportedEncodingException {
        JSONObject fields= FieldHandle.readFormMappingProcessed();
        SafeHarbor safeHarbor=new SafeHarbor();
        ArrayList<String> removeField=jsonArrayToArrayList(fields.getJSONArray("Sensitive"));
        removeField.addAll(jsonArrayToArrayList(fields.getJSONArray("Other_Soft")));
        data=safeHarbor.identity(data,removeField,
                jsonArrayToArrayList(fields.getJSONArray("Date")),
                jsonArrayToArrayList(fields.getJSONArray("Geographic")),
                jsonArrayToArrayList(fields.getJSONArray("Other_Middle")),
                jsonArrayToArrayList(fields.getJSONArray("Unstructured_Data")));
        return data;
    }


    /**
     * 用于外部调用有限数据集方法
     * @param data
     * @param data
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<ArrayList<String>> ToLimitedSet(ArrayList<ArrayList<String>> data) throws FileNotFoundException, UnsupportedEncodingException {
        JSONObject fields= FieldHandle.readFormMappingProcessed();
        LimitedSet limitedSet=new LimitedSet();
        data=limitedSet.identity(data,
                jsonArrayToArrayList(fields.getJSONArray("Sensitive")),
                jsonArrayToArrayList(fields.getJSONArray("Date")),
                jsonArrayToArrayList(fields.getJSONArray("Geographic")),
                jsonArrayToArrayList(fields.getJSONArray("Unstructured_Data")));
        return data;
    }

    /**
     * 用于jsonArray与ArrayList之间的转换
     * @param jsonArray
     * @return
     */
    private static ArrayList<String> jsonArrayToArrayList(JSONArray jsonArray){
        ArrayList<String> out=new ArrayList<String>();
        if(jsonArray==null) return out;
        for(int i=0;i<jsonArray.size();i++){
            out.add(jsonArray.getString(i));
        }
        return out;
    }

}
