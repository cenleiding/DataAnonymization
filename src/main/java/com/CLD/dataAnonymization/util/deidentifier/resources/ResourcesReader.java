package com.CLD.dataAnonymization.util.deidentifier.resources;

import com.CLD.dataAnonymization.util.deidentifier.io.FileResolve;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 该类用于读取资源文件
 * @Author CLD
 * @Date 2018/5/10 14:28
 **/
public class ResourcesReader {

    /**
     * 读取资源文件中的地理信息文件
     * @return
     */
    public static HashMap<String,ArrayList<String>> readAddress() throws FileNotFoundException {
        InputStream is=new Object(){
            public InputStream get(){
                return this.getClass().getResourceAsStream("./Address.json");
//                return this.getClass().getClassLoader().getResourceAsStream("com/CLD/dataAnonymization/util/deidentifier/resources/Address.json");
            }
        }.get();
        JSONObject jsonObject= FileResolve.readerObjectJson(is);
        HashMap<String,ArrayList<String>> addressList=new HashMap<String,ArrayList<String>>();
        ArrayList<String> bigCity=jsonArrayToArrayList(jsonObject.getJSONArray("BigCity"));
        ArrayList<String> smallCity=jsonArrayToArrayList(jsonObject.getJSONArray("SmallCity"));
        addressList.put("bigCity",bigCity);
        addressList.put("smallCity",smallCity);
        return addressList;
    }

    /**
     * 读取资源文件中的默认字段表
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<ArrayList<String>> readFields() throws FileNotFoundException {
        InputStream is=new Object(){
            public InputStream get(){
                return this.getClass().getResourceAsStream("./Original.json");
            }
        }.get();
        JSONArray jsonArray=FileResolve.readerArrayJson(is);
        ArrayList<ArrayList<String>> fieldsList=new ArrayList<ArrayList<String>>();
        for (int i=0;i<jsonArray.size();i++){
            ArrayList<String> field=new ArrayList<String>();
            field.add(jsonArray.getJSONObject(i).getString("fieldName"));
            field.add(jsonArray.getJSONObject(i).getString("fieldType"));
            fieldsList.add(field);
        }
        return fieldsList;
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
