package com.CLD.dataAnonymization.util.deidentifier.io;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import java.io.*;

/**
 * @description:用于读取json文件
 * @Author CLD
 * @Date 2018/8/2 16:12
 */
public class JsonReader {


    /**
     * 读取jsonObject 文件
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static JSONObject jsonObjectReader(String path) throws FileNotFoundException {
        InputStream is=new FileInputStream(path);
        return jsonObjectReader(is);
    }

    /**
     * 读取jsonArray 文件
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static JSONArray jsonArrayReader(String path) throws FileNotFoundException {
        InputStream is=new FileInputStream(path);
        return jsonArrayReader(is);
    }

    /**
     * 读取jsonObject 文件
     * @param is
     * @return
     */
    public static JSONObject jsonObjectReader(InputStream is){
        JSONObject jsonObject=new JSONObject();
        try {
            JSONReader reader=new JSONReader(new InputStreamReader(is,"UTF-8"));
            jsonObject= (JSONObject) reader.readObject();
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 读取jsonArray 文件
     * @param is
     * @return
     */
    public static JSONArray jsonArrayReader(InputStream is){
        JSONArray jsonArray=new JSONArray();
        try {
            JSONReader reader=new JSONReader(new InputStreamReader(is,"UTF-8"));
            jsonArray= (JSONArray) reader.readObject();
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
