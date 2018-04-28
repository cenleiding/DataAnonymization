package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.dao.h2.entity.ApiUsage;
import com.CLD.dataAnonymization.dao.h2.repository.ApiUsageRepository;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 该类用于读写API使用情况日志
 * @Author CLD
 * @Date 2018/4/16 16:57
 **/
@Service
public class ApiUsageServiceImpl implements ApiUsageService {

    ReadWriteLock readWriteLock=new ReentrantReadWriteLock();

    @Autowired
    ApiUsageRepository apiUsageRepository;

    @Override
    public Boolean addUsageLog(String ip, String time, ArrayList<String> head, String size,String method) throws UnsupportedEncodingException {

        String shead="";
        for(String s:head) shead+=s+";";
        ApiUsage apiUsage=new ApiUsage();
        apiUsage.setIp(ip);
        apiUsage.setTime(time);
        apiUsage.setHead(shead);
        apiUsage.setSize(size);
        apiUsage.setMethod(method);
        apiUsageRepository.save(apiUsage);
        return true;
    }

    @Override
    public JSONArray getUsageOverView() throws FileNotFoundException {
        Map<String,Map<String,Integer>> map=new HashMap<String,Map<String, Integer>>();
        JSONArray jsonArray=new JSONArray();
        List<ApiUsage> usageList = apiUsageRepository.findAll();
        for(ApiUsage usage:usageList){
            if(map.keySet().contains(usage.getIp())){
                if(map.get(usage.getIp()).keySet().contains(usage.getTime().substring(0,10))){
                    HashMap<String,Integer> m= (HashMap<String, Integer>) map.get(usage.getIp());
                    m.put(usage.getTime().substring(0,10),m.get(usage.getTime().substring(0,10))+1);
                    map.put(usage.getIp(),m);
                }else{
                    HashMap<String,Integer> m= (HashMap<String, Integer>) map.get(usage.getIp());
                    m.put(usage.getTime().substring(0,10),1);
                    map.put(usage.getIp(),m);
                }

            }else {
                Map<String,Integer> m=new HashMap<String,Integer>();
                m.put(usage.getTime().substring(0,10),1);
                map.put(usage.getIp(),m);
            }
        }
        for (String k:map.keySet()) {
            JSONObject jo=new JSONObject();
            JSONArray ja=new JSONArray();
            jo.put("name",k);
            for(String s:map.get(k).keySet()){
                JSONArray j=new JSONArray();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.valueOf(s.substring(0,4)), Integer.valueOf(s.substring(5,7))-1, Integer.valueOf(s.substring(8,10)));
                j.add(calendar.getTimeInMillis());
                j.add(map.get(k).get(s));
                ja.add(j);
            }
            for (int i = 0; i < ja.size()-1; i++) {
                for (int j = i+1; j <ja.size() ; j++) {
                    if(ja.getJSONArray(i).getLong(0)>ja.getJSONArray(j).getLong(0)){
                        JSONArray m=ja.getJSONArray(i);
                        ja.set(i,ja.getJSONArray(j));
                        ja.set(j,m);
                    }
                }
            }
            jo.put("data",ja);
            jsonArray.add(jo);
        }
        return jsonArray;
    }

    @Override
    public ArrayList<HashMap<String,String>> getUserDetail(String Ip) {
        ArrayList<HashMap<String,String>> hashMapArrayList=new ArrayList<HashMap<String, String>>();
        List<ApiUsage> usageList=apiUsageRepository.findAllByIp(Ip);
        int id=0;
        for(ApiUsage usage:usageList){
            HashMap<String,String> hashMap=new HashMap<String,String>();
            hashMap.put("id",String.valueOf(id++));
                hashMap.put("time",usage.getTime());
                hashMap.put("field",usage.getHead());
                hashMap.put("num",usage.getSize());
                hashMap.put("method",usage.getMethod());
                hashMapArrayList.add(hashMap);
        }

        return hashMapArrayList;
    }

    @Override
    public Set<String> getUserIp() {
        Set<String> set=new HashSet<String>();
        List<String> ipList =apiUsageRepository.findAllIp();
        for(String ip:ipList){
            set.add(ip);
        }
        return set;
    }

}
