package com.CLD.dataAnonymization.util.deidentifier;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * @Author CLD
 * @Date 2018/5/10 14:28
 **/
public class FieldHandle {

    /**
     * @return
     */
    public static ArrayList<ArrayList<String>> readAddress() {
        InputStream is= null;
        JSONObject outJson=new JSONObject();
        try {
            is=new Object(){
                public InputStream get(){
                    return this.getClass().getClassLoader().getResourceAsStream("com/CLD/dataAnonymization/util/deidentifier/Resources/Address.json");
                }
            }.get();
            JSONReader reader=new JSONReader(new InputStreamReader(is,"UTF-8"));
            outJson= (JSONObject) reader.readObject();
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ArrayList<ArrayList<String>> addressList=new ArrayList<ArrayList<String>>();
        ArrayList<String> bigCity=jsonArrayToArrayList(outJson.getJSONArray("BigCity"));
        ArrayList<String> smallCity=jsonArrayToArrayList(outJson.getJSONArray("SmallCity"));
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
