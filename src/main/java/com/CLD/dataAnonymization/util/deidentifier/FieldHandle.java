package com.CLD.dataAnonymization.util.deidentifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author CLD
 * @Date 2018/5/10 14:28
 **/
public class FieldHandle {

    private static final String FilePath_mapping=new Object() {
        public String get(){
            return this.getClass().getClassLoader().getResource("").getPath();
        }
    }.get().replaceAll("target/classes/","")
            .replaceAll("1.jar!/BOOT-INF/classes!/","")
            .replaceAll("file:","")+"resources/Form_mapping.json";

    private static final String FilePath_address=new Object() {
        public String get(){
            return this.getClass().getClassLoader().getResource("").getPath();
        }
    }.get().replaceAll("target/classes/","")
            .replaceAll("1.jar!/BOOT-INF/classes!/","")
            .replaceAll("file:","")+"resources/Address.json";


    /**
     * @return
     */
    public static ArrayList<ArrayList<String>> readAddress() {
        InputStream is= null;
        JSONObject outJson=new JSONObject();
        try {
            is = new FileInputStream(FilePath_address);
            JSONReader reader=new JSONReader(new InputStreamReader(is,"UTF-8"));
            outJson= (JSONObject) reader.readObject();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
     * 读取字段映射表
     * 返回模板分类
     * @return JSONArray
     */
    public static JSONArray readFormMappingOriginal() throws FileNotFoundException, UnsupportedEncodingException {
        InputStream is=new FileInputStream(FilePath_mapping);
        JSONArray outJson=new JSONArray();
        JSONReader reader=new JSONReader(new InputStreamReader(is,"UTF-8"));
        reader.startArray();
        while(reader.hasNext()) {
            JSONObject ja= (JSONObject) reader.readObject();
            outJson.add(ja);
        }
        reader.endArray();
        reader.close();
        System.out.println("获取源数据："+outJson);
        return outJson;
    }

    /**
     * 读取字段映射表
     * 返回21种分类
     * @return
     */
    public static JSONObject readFormMappingClassified() throws FileNotFoundException, UnsupportedEncodingException {
        return readFormMappingClassified(readFormMappingOriginal());
    }


    /**
     * 读取字段映射表
     * 返回21种分类
     * @return
     */
    public static JSONObject readFormMappingClassified(JSONArray jsonArray) throws FileNotFoundException {
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

        for(int i=0;i<jsonArray.size();i++)
            for(int j=0;j<jsonArray.getJSONObject(i).getJSONArray("fields").size();j++){
                JSONArray  db=jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getJSONArray("Db_field");
                JSONArray  en=jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getJSONArray("En_field");
                JSONArray  ch=jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getJSONArray("Ch_field");
                for(int k=0;k<db.size();k++)if(db.getString(k)!=null)
                    out.get(jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("type")).add(db.getString(k));
                for(int k=0;k<en.size();k++)if(en.getString(k)!=null)
                    out.get(jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("type")).add(en.getString(k));
                for(int k=0;k<ch.size();k++)if(ch.getString(k)!=null)
                    out.get(jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("type")).add(ch.getString(k));
            }


        return JSONObject.parseObject(JSON.toJSONString(out));
    }

    /**
     * 读取字段映射表
     * 返回6种分类
     * @return
     * @throws FileNotFoundException
     */
    public static JSONObject readFormMappingProcessed() throws FileNotFoundException, UnsupportedEncodingException {
        return readFormMappingProcessed(readFormMappingClassified());

    }

    /**
     * 读取字段映射表
     * 返回6种分类
     * @return
     */
    public static JSONObject readFormMappingProcessed(JSONObject jsonObject) throws FileNotFoundException {
        JSONObject outjson=new JSONObject();
        jsonObject=ToStandard(jsonObject);
        for(String key:jsonObject.keySet()){
            if(key=="Date"){
                outjson.put("Date",new HashSet<String>(jsonArrayToArrayList(jsonObject.getJSONArray(key))));
                continue;
            }
            if(key=="Geographic"){
                outjson.put("Geographic",new HashSet<String>(jsonArrayToArrayList(jsonObject.getJSONArray(key))));
                continue;
            }
            if(key=="Other_Middle"){
                outjson.put("Other_Middle",new HashSet<String>(jsonArrayToArrayList(jsonObject.getJSONArray(key))));
                continue;
            }
            if(key=="Other_Soft"){
                outjson.put("Other_Soft",new HashSet<String>(jsonArrayToArrayList(jsonObject.getJSONArray(key))));
                continue;
            }
            if(key=="Unstructured_Data"){
                outjson.put("Unstructured_Data",new HashSet<String>(jsonArrayToArrayList(jsonObject.getJSONArray(key))));
                continue;
            }
            Set<String> set=new HashSet<String>();
            set.addAll(jsonArrayToArrayList(jsonObject.getJSONArray(key)));
            set.addAll(jsonArrayToArrayList(outjson.getJSONArray("Sensitive")));
            outjson.put("Sensitive",set);
        }

        return outjson;
    }

    /**
     * 用于更新匿名字段
     * @param jsonArray 模板分类数据
     * @return
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<String> updataFieldFile(JSONArray jsonArray) throws UnsupportedEncodingException, FileNotFoundException {
        ArrayList<String> arrayList=checkFields(jsonArray);
        if(arrayList.size()!=0) return arrayList;
        try {
            InputStream in =new ByteArrayInputStream(jsonArray.toJSONString().getBytes(StandardCharsets.UTF_8));
            FileOutputStream out = new FileOutputStream(FilePath_mapping);
            byte buffer[] = new byte[4*1024];
            int len = 0;
            while((len=in.read(buffer))>0){
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            arrayList.add("更新匿名字段失败（文件路径错误）！");
            return arrayList;
        } catch (IOException e) {
            e.printStackTrace();
            arrayList.add("更新匿名字段失败（文件写入错误）！");
            return arrayList;
        }
        System.out.println("Form_mapping:更新成功");
        arrayList.add("更新匿名字段成功！");
        return arrayList;
    }

    /**
     * 用于检查匿名字段是否有冲突
     * @param jsonArray
     * @return
     */
    private static ArrayList<String> checkFields(JSONArray jsonArray) throws FileNotFoundException {
        JSONObject jsonObject=readFormMappingProcessed(readFormMappingClassified(jsonArray));
        Set set=new HashSet();
        ArrayList<String> arrayList=new ArrayList<String>();
        for(Object s:jsonObject.getJSONArray("Unstructured_Data")){
            if(set.contains((String)s)) arrayList.add((String) s+"类型冲突\r\n");
            set.add((String)s);
        }
        for(Object s:jsonObject.getJSONArray("Other_Soft")){
            if(set.contains((String)s)) arrayList.add((String) s+"类型冲突\r\n");
            set.add((String)s);
        }
        for(Object s:jsonObject.getJSONArray("Other_Middle")){
            if(set.contains((String)s)) arrayList.add((String) s+"类型冲突\r\n");
            set.add((String)s);
        }
        for(Object s:jsonObject.getJSONArray("Geographic")){
            if(set.contains((String)s)) arrayList.add((String) s+"类型冲突\r\n");
            set.add((String)s);
        }
        for(Object s:jsonObject.getJSONArray("Date")){
            if(set.contains((String)s)) arrayList.add((String) s+"类型冲突\r\n");
            set.add((String)s);
        }
        for(Object s:jsonObject.getJSONArray("Sensitive")){
            if(set.contains((String)s)) arrayList.add((String) s+"类型冲突\r\n");
            set.add((String)s);
        }
        return arrayList;
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

    /**
     * 用于标准化匿名字段
     * 统一变为小写、无分隔符
     * @param jsonObject
     * @return
     */
    private static JSONObject ToStandard(JSONObject jsonObject){
        for(String key:jsonObject.keySet()){
            JSONArray jsonArray=jsonObject.getJSONArray(key);
            for(int i=0;i<jsonArray.size();i++){
                jsonArray.set(i,jsonArray.getString(i).toLowerCase()
                        .replace("_","")
                        .replace("-","")
                        .replace(".","")
                        .replace("*","")
                );
            }
        }
        return jsonObject;
    }

}
