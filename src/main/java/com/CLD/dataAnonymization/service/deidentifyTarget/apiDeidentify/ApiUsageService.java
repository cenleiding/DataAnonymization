package com.CLD.dataAnonymization.service.deidentifyTarget.apiDeidentify;

import com.alibaba.fastjson.JSONArray;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @description:该类用于读写API使用情况日志
 * @Author CLD
 * @Date 2018/4/16 16:14
 **/
public interface ApiUsageService {

    /**
     * 该方法用于添加使用记录
     * @param ip
     * @param time
     * @param head
     * @param size
     * @param method
     * @return
     */
    public Boolean addUsageLog(String ip, String time, ArrayList<String> head,String size,String method) throws UnsupportedEncodingException;

    /**
     * 获取接口使用情况总览
     * @return
     */
    public JSONArray getUsageOverView() throws FileNotFoundException;

    /**
     * 根据Ip获得相应使用详细情况
     * @param Ip
     * @return
     */
    public ArrayList<HashMap<String,String>> getUserDetail(String Ip);

    /**
     * 获得使用者Ip集合
     * @return
     */
    public Set<String> getUserIp();
}
