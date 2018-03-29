package com.CLD.dataAnonymization.service;

import com.alibaba.fastjson.JSONArray;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * 该类用于对数据结构进行解析，进行数据格式之间的转换。
 * @Author CLD
 * @Date 2018/3/29 14:31
 **/
public interface DataParseService {

    /**
     * 该方法将对HttpServletRequest body中的数据进行解析，返回ArrayList二维列表类型数据
     * @param req
     * @return
     */
    public ArrayList<ArrayList<String>> requestDataToArrayList(HttpServletRequest req);

    /**
     * 该方法将对ArrayList<ArrayList<String>>类型的数据进行解析，返回JSONArray类型的数据
     * @param arr
     * @return
     */
    public JSONArray ArrayListToJsonArray(ArrayList<ArrayList<String>> arr);


}
