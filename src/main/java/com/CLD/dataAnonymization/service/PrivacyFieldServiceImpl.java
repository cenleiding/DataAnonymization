package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.model.ProcessingFields;
import com.CLD.dataAnonymization.util.deidentifier.FileResolve;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 该类用于对隐私字段的获取，展示和修改
 * @Author CLD
 * @Date 2018/4/2 14:00
 **/
@Service
public class PrivacyFieldServiceImpl implements PrivacyFieldService {

    @Override
    public JSONArray getPrivaryFields() throws FileNotFoundException {
        JSONArray outJson=new JSONArray();
        String URL =this.getClass().getResource("/").getPath()+ "com/CLD/dataAnonymization/util/deidentifier/Resources/Form_mapping.json";
        JSONReader reader=new JSONReader(new FileReader(URL));
        reader.startArray();
        while(reader.hasNext()) {
            JSONObject ja= (JSONObject) reader.readObject();
            outJson.add(ja);
        }
        reader.endArray();
        reader.close();
        return outJson;
    }

    @Override
    public JSONObject getOrganizedFields() throws FileNotFoundException {
        return getOrganizedFields(getPrivaryFields());
    }

    @Override
    public JSONObject getOrganizedFields(JSONArray jsonArray) {
        HashMap<String, HashSet<String>> out=new HashMap<String,HashSet<String>>();
        out.put("Names",new HashSet<>());
        out.put("Geographic",new HashSet<>());
        out.put("Date",new HashSet<>());
        out.put("Telephone_Numbers",new HashSet<>());
        out.put("Fax_Numbers",new HashSet<>());
        out.put("Email_Addresses",new HashSet<>());
        out.put("Social_Security_Numbers",new HashSet<>());
        out.put("Medical_Record_Numbers",new HashSet<>());
        out.put("Health_Plan_Beneficiary_Numbers",new HashSet<>());
        out.put("Account_Numbers",new HashSet<>());
        out.put("Certificate_License_Numbers",new HashSet<>());
        out.put("Vehicle_Identifiers",new HashSet<>());
        out.put("Device_Identifiers",new HashSet<>());
        out.put("Url",new HashSet<>());
        out.put("Ip",new HashSet<>());
        out.put("Biometric_Identifiers",new HashSet<>());
        out.put("Photographs",new HashSet<>());
        out.put("Other_Hard",new HashSet<>());
        out.put("Other_Middle",new HashSet<>());
        out.put("Other_Soft",new HashSet<>());
        out.put("Unstructured_Data",new HashSet<>());

        for(int i=0;i<jsonArray.size();i++){
            JSONObject b1=jsonArray.getJSONObject(i);
            JSONArray  a1=b1.getJSONArray("fields");
            for(int j=0;j<a1.size();j++){
                JSONObject b2=a1.getJSONObject(j);
                out.get(b2.getString("type")).add(b2.getString("Database_field"));
                out.get(b2.getString("type")).add(b2.getString("English_field"));
                out.get(b2.getString("type")).add(b2.getString("Chinese_field"));
            }
        }

        return JSONObject.parseObject(JSON.toJSONString(out));
    }

    @Override
    public ProcessingFields getProcessingFields(JSONObject jsonObject) {
        jsonObject=ToStandard(jsonObject);
        ProcessingFields processingFields=new ProcessingFields();
        for(String key:jsonObject.keySet()){
            if(key=="Date") {processingFields.getDate().addAll(new HashSet<String>(jsonArrayToArrayList(jsonObject.getJSONArray(key))));continue;}
            if(key=="Geographic"){processingFields.getGeographic().addAll(new HashSet<String>(jsonArrayToArrayList(jsonObject.getJSONArray(key))));continue;}
            if(key=="Other_Middle"){processingFields.getOther_middle().addAll(new HashSet<String>(jsonArrayToArrayList(jsonObject.getJSONArray(key))));continue;}
            if(key=="Other_Soft"){processingFields.getOther_soft().addAll(new HashSet<String>(jsonArrayToArrayList(jsonObject.getJSONArray(key))));continue;}
            if(key=="Unstructured_Data"){processingFields.getUnstructured().addAll(new HashSet<String>(jsonArrayToArrayList(jsonObject.getJSONArray(key))));continue;}
            //确保敏感信息字段唯一
            Set<String> set=new HashSet<String>();
            ArrayList<String> arrayList=new ArrayList<String>();
            set.addAll(processingFields.getSensitive());
            set.addAll(jsonArrayToArrayList(jsonObject.getJSONArray(key)));
            arrayList.addAll(set);
            processingFields.setSensitive(arrayList);
        }
        return processingFields;
    }

    @Override
    public ProcessingFields getProcessingFields(JSONArray jsonArray) {
        return getProcessingFields(getOrganizedFields(jsonArray));
    }

    @Override
    public ProcessingFields getProcessingFields() throws FileNotFoundException {
        return getProcessingFields(getOrganizedFields());
    }

    @Override
    public ArrayList<String> checkFields(JSONArray jsonArray) {
        return checkFields(getProcessingFields(jsonArray));
    }

    @Override
    public ArrayList<String> checkFields(ProcessingFields processingFields)  {
        Set set=new HashSet();
        ArrayList<String> arrayList=new ArrayList<String>();
        for(String s:processingFields.getUnstructured()){
            if(set.contains(s)) arrayList.add(s);
            set.add(s);
        }
        for(String s:processingFields.getOther_soft()){
            if(set.contains(s)) arrayList.add(s);
            set.add(s);
        }
        for(String s:processingFields.getOther_middle()){
            if(set.contains(s)) arrayList.add(s);
            set.add(s);
        }
        for(String s:processingFields.getGeographic()){
            if(set.contains(s)) arrayList.add(s);
            set.add(s);
        }
        for(String s:processingFields.getDate()){
            if(set.contains(s)) arrayList.add(s);
            set.add(s);
        }
        for(String s:processingFields.getSensitive()){
            if(set.contains(s)) arrayList.add(s);
            set.add(s);
        }

        return arrayList;
    }

    @Override
    public boolean updataFieldFile(JSONArray jsonArray) throws UnsupportedEncodingException {
        String URL =this.getClass().getResource("/").getPath()+ "com/CLD/dataAnonymization/util/deidentifier/Resources/Form_mapping_2.json";
        String path= URLDecoder.decode(URL, "utf-8");
        System.out.println(path);
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
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Form_mapping:保存成功");
        return true;
    }

    public ArrayList<String> jsonArrayToArrayList(JSONArray jsonArray){
        ArrayList<String> out=new ArrayList<String>();
        for(int i=0;i<jsonArray.size();i++){
            out.add(jsonArray.getString(i));
        }
        return out;
    }

    /**
     * 用于标准化匿名字段
     * 统一变为小写、无分隔符
     * @param jsonObject
     * @return
     */
    public JSONObject ToStandard(JSONObject jsonObject){
        for(String key:jsonObject.keySet()){
           JSONArray jsonArray=jsonObject.getJSONArray(key);
           for(int i=0;i<jsonArray.size();i++){
               jsonArray.set(i,jsonArray.getString(i).toLowerCase().replace("_","").replace("-",""));
           }
        }
        return jsonObject;
    }

}
