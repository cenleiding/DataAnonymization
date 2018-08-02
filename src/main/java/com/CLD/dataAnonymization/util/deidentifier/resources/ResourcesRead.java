package com.CLD.dataAnonymization.util.deidentifier.resources;

import com.CLD.dataAnonymization.util.deidentifier.io.FileResolve;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * 该类用于读取资源文件
 * @Author CLD
 * @Date 2018/5/10 14:28
 **/
public class ResourcesRead {

    /**
     * 读取资源文件中的地理信息文件
     * @return
     */
    public static ArrayList<ArrayList<String>> readAddress() throws FileNotFoundException {
        InputStream is=new Object(){
            public InputStream get(){
                return this.getClass().getResourceAsStream("./Address.json");
//                return this.getClass().getClassLoader().getResourceAsStream("com/CLD/dataAnonymization/util/deidentifier/resources/Address.json");
            }
        }.get();
        JSONObject jsonObject= FileResolve.readerObjectJson(is);
        ArrayList<ArrayList<String>> addressList=new ArrayList<ArrayList<String>>();
        ArrayList<String> bigCity=jsonArrayToArrayList(jsonObject.getJSONArray("BigCity"));
        ArrayList<String> smallCity=jsonArrayToArrayList(jsonObject.getJSONArray("SmallCity"));
        addressList.add(bigCity);
        addressList.add(smallCity);
        return addressList;
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
