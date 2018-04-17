package com.CLD.dataAnonymization.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    @Override
    public Boolean addUsageLog(String ip, String time, ArrayList<String> head, String size,String method) throws UnsupportedEncodingException {
        JSONArray jsonArray=new JSONArray();
        JSONReader jsonReader=readJson();
        jsonReader.startArray();
        while (jsonReader.hasNext()){
            jsonArray.add(jsonReader.readObject());
        }
        jsonReader.endArray();
        jsonReader.close();
        JSONArray ja=new JSONArray();
        JSONArray j=new JSONArray();
        ja.add(ip);
        ja.add(time);
        j.addAll(head);
        ja.add(j);
        ja.add(size);
        ja.add(method);
        jsonArray.add(ja);

        String path= URLDecoder.decode(this.getClass().getResource("/").getPath()+"com/CLD/dataAnonymization/AppData/ApiUsageLog.json", "utf-8");
        System.out.println(path);
        readWriteLock.writeLock().lock();
        try {
            InputStream in =new ByteArrayInputStream(jsonArray.toJSONString().getBytes(StandardCharsets.UTF_8));
            FileOutputStream out = new FileOutputStream(path);
            byte buffer[] = new byte[4*1024];
            int len = 0;
            while((len=in.read(buffer))>0){
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("ApiUsageLog:保存失败");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ApiUsageLog:保存失败");
            return false;
        }finally {
            readWriteLock.writeLock().unlock();
        }

        System.out.println("ApiUsageLog:保存成功");
        return true;
    }

    @Override
    public JSONArray getUsageOverView() throws FileNotFoundException {
        Map<String,Map<String,Integer>> map=new HashMap<String,Map<String, Integer>>();
        JSONArray jsonArray=new JSONArray();
        JSONReader jsonReader=readJson();
        jsonReader.startArray();
        while(jsonReader.hasNext()){
            JSONArray ja= (JSONArray) jsonReader.readObject();
            if(map.keySet().contains(ja.getString(0))){
                if(map.get(ja.getString(0)).keySet().contains(ja.getString(1).substring(0,10))){
                    HashMap<String,Integer> m= (HashMap<String, Integer>) map.get(ja.getString(0));
                    m.put(ja.getString(1).substring(0,10),m.get(ja.getString(1).substring(0,10))+1);
                    map.put(ja.getString(0),m);
                }else{
                    HashMap<String,Integer> m= (HashMap<String, Integer>) map.get(ja.getString(0));
                    m.put(ja.getString(1).substring(0,10),1);
                    map.put(ja.getString(0),m);
                }

            }else {
                Map<String,Integer> m=new HashMap<String,Integer>();
                m.put(ja.getString(1).substring(0,10),1);
                map.put(ja.getString(0),m);
            }
        }
        jsonReader.endArray();
        jsonReader.close();
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
        JSONReader jsonReader=readJson();
        int id=0;
        jsonReader.startArray();
        while (jsonReader.hasNext()){
            JSONArray jsonArray= (JSONArray) jsonReader.readObject();
            if(Ip.equals(jsonArray.getString(0))){
                HashMap<String,String> hashMap=new HashMap<String,String>();
                hashMap.put("id",String.valueOf(id++));
                hashMap.put("time",jsonArray.getString(1));
                hashMap.put("field",jsonArray.getString(2));
                hashMap.put("num",jsonArray.getString(3));
                hashMap.put("method",jsonArray.getString(4));
                hashMapArrayList.add(hashMap);
            }
        }
        return hashMapArrayList;
    }

    @Override
    public Set<String> getUserIp() {
        Set<String> set=new HashSet<String>();
        JSONReader jsonReader=readJson();
        jsonReader.startArray();
        while (jsonReader.hasNext()){
            set.add(((JSONArray) jsonReader.readObject()).getString(0));
        }
        return set;
    }

    public JSONReader readJson(){
        String path=this.getClass().getResource("/").getPath()+"com/CLD/dataAnonymization/AppData/ApiUsageLog.json";
        JSONReader jsonReader=null;
        readWriteLock.readLock().lock();
        try{
            jsonReader=new JSONReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            readWriteLock.readLock().unlock();
        }
        return jsonReader;
    }
}
