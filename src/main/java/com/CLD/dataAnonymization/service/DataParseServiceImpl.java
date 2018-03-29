package com.CLD.dataAnonymization.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;

import static com.sun.xml.internal.stream.writers.XMLStreamWriterImpl.UTF_8;

/**
 * 该类用于对数据结构进行解析，进行数据格式之间的转换。
 * @Author CLD
 * @Date 2018/3/29 14:52
 **/
@Service
public class DataParseServiceImpl implements DataParseService {

    @Override
    public ArrayList<ArrayList<String>> requestDataToArrayList(HttpServletRequest req) {
        ArrayList<ArrayList<String>> outList=new ArrayList<ArrayList<String>>();
        ArrayList<String> singleList=new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String reqBody = URLDecoder.decode(sb.toString(), UTF_8);
            JSONArray jsonArray = com.alibaba.fastjson.JSON.parseArray(reqBody);
            for(int i=0;i<jsonArray.size();i++){
               JSONObject jsonObject=jsonArray.getJSONObject(i);
               if(i==0){
                   for(String key:jsonObject.keySet())
                       singleList.add(key);
                   outList.add((ArrayList<String>) singleList.clone());
                   singleList.clear();
               }
               for(Object value:jsonObject.values())
                   singleList.add(value.toString());
               outList.add((ArrayList<String>) singleList.clone());
               singleList.clear();
            }
            System.out.println(req.getRemoteAddr()+": 数据转换完毕...开始匿名化");
        } catch (Exception e) {
            return null;
        }
        return outList;
    }

    @Override
    public JSONArray ArrayListToJsonArray(ArrayList<ArrayList<String>> arr) {
        StringBuilder jsonStr=new StringBuilder();
        jsonStr.append("[");
        for(int i=1;i<arr.size();i++){
            jsonStr.append("{");
            for(int j=0;j<arr.get(0).size();j++){
                jsonStr.append("\""+arr.get(0).get(j)+"\"");
                jsonStr.append(":");
                jsonStr.append("\""+arr.get(i).get(j)+"\",");
            }
            jsonStr.deleteCharAt(jsonStr.length()-1);
            jsonStr.append("},");
        }
        jsonStr.deleteCharAt(jsonStr.length()-1);
        jsonStr.append("]");
        return JSON.parseArray(String.valueOf(jsonStr));
    }
}
