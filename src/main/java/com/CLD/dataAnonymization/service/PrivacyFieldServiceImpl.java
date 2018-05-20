package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.model.NodeInfo;
import com.CLD.dataAnonymization.util.deidentifier.FieldHandle;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author CLD
 * @Date 2018/5/10 9:29
 **/
@Service
public class PrivacyFieldServiceImpl implements PrivacyFieldService {

    @Value("${clever.template.deploy}")
    private String clever_template_deploy;
            //="http://172.16.100.63:8000/clever-rest/template/deploy/record/latest";

    @Value("${clever.template.mapping}")
    private String clever_template_mapping;
    //="http://172.16.100.63:8000/clever-rest/template/mapping/";


    @Override
    public JSONArray getPrivaryFields() throws FileNotFoundException, UnsupportedEncodingException {
        return FieldHandle.readFormMappingOriginal();
    }

    @Override
    public JSONObject getOrganizedFields() throws FileNotFoundException, UnsupportedEncodingException {
        return FieldHandle.readFormMappingClassified();
    }

    @Override
    public JSONObject getProcessingFields() throws FileNotFoundException, UnsupportedEncodingException {
        return FieldHandle.readFormMappingProcessed();
    }

    @Override
    public ArrayList<String> updataFieldFile(JSONArray jsonArray) throws UnsupportedEncodingException, FileNotFoundException {
        return FieldHandle.updataFieldFile(jsonArray);
    }

    @Override
    public Boolean pollField() throws FileNotFoundException, UnsupportedEncodingException {
        List<String[]> templateNames=GetTemplateName();
        List<NodeInfo> nodeList=new ArrayList<NodeInfo>();
        for(String[] name:templateNames){
            nodeList.addAll(GetTemplateMapping(name[0],name[1]));
        }
        JSONArray jsonArray=getPrivaryFields();
        Map<String,Integer[]> nodeMap=new HashMap<String,Integer[]>();
        for(int i=0;i<jsonArray.size();i++)
            for(int j=0;j<jsonArray.getJSONObject(i).getJSONArray("fields").size();j++){
            nodeMap.put(jsonArray.getJSONObject(i).getString("archetypeId")+
                    jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("nodePath"),
                    new Integer[]{i,j});
            }

        for(int i=0;i<nodeList.size();i++){
            String pathAndId=nodeList.get(i).getSpecialiseArchetypeId()+nodeList.get(i).getNodePath();
            String db_field=nodeList.get(i).getEn_name();
            String en_field=db_field.toLowerCase()
                    .replace(".","")
                    .replace("_","")
                    .replace("-","")
                    .replace("*","");
            String ch_field=nodeList.get(i).getCh_name();
            if(ch_field!=null) ch_field=ch_field
                    .replace(".","")
                    .replace("_","")
                    .replace("-","")
                    .replace("*","");
            if(nodeMap.keySet().contains(pathAndId)){
                JSONArray db=jsonArray.getJSONObject(nodeMap.get(pathAndId)[0]).getJSONArray("fields").getJSONObject(nodeMap.get(pathAndId)[1]).getJSONArray("Db_field");
                JSONArray ch=jsonArray.getJSONObject(nodeMap.get(pathAndId)[0]).getJSONArray("fields").getJSONObject(nodeMap.get(pathAndId)[1]).getJSONArray("Ch_field");
                JSONArray en=jsonArray.getJSONObject(nodeMap.get(pathAndId)[0]).getJSONArray("fields").getJSONObject(nodeMap.get(pathAndId)[1]).getJSONArray("En_field");
                if(!db.contains(db_field)&&db_field!=null&&!db_field.equals(""))
                    jsonArray.getJSONObject(nodeMap.get(pathAndId)[0]).getJSONArray("fields").getJSONObject(nodeMap.get(pathAndId)[1]).getJSONArray("Db_field").add(db_field);
                if(!en.contains(en_field)&&db_field!=null&&!db_field.equals(""))
                    jsonArray.getJSONObject(nodeMap.get(pathAndId)[0]).getJSONArray("fields").getJSONObject(nodeMap.get(pathAndId)[1]).getJSONArray("En_field").add(en_field);
                if(!ch.contains(ch_field)&&ch_field!=null&&!ch_field.equals(""))
                    jsonArray.getJSONObject(nodeMap.get(pathAndId)[0]).getJSONArray("fields").getJSONObject(nodeMap.get(pathAndId)[1]).getJSONArray("Ch_field").add(ch_field);
            }
        }
        if (!FieldHandle.updataFieldFile(jsonArray).get(0).equals("更新匿名字段成功！")){
            System.out.println("字段自动更新失败");
            return false;
        }else{
            System.out.println("字段自动更新成功");
            return true;
        }
    }


    private List<String[]> GetTemplateName(){
        ArrayList<String[]> templateName=new ArrayList<String[]>();
        String context=HttpGet(clever_template_deploy);
        JSONObject jsonObject= JSON.parseObject(context);
        for(int i=0;i<jsonObject.getJSONArray("data").size();i++){
            String[] strings=new String[]{
                    jsonObject.getJSONArray("data")
                            .getJSONObject(i)
                            .getJSONArray("deployTemplates")
                            .getJSONObject(0)
                            .getString("templateName"),
                    jsonObject.getJSONArray("data")
                            .getJSONObject(i)
                            .getJSONArray("deployTemplates")
                            .getJSONObject(0)
                            .getJSONObject("master")
                            .getString("specialiseArchetypeId")};
            templateName.add(strings);
        }
        System.out.println("获得已部署模板："+templateName.size());
        return templateName;
    }

    private List<NodeInfo> GetTemplateMapping(String TemplateName,String specialiseArchetypeId){
        List<NodeInfo> list=new ArrayList<NodeInfo>();
        String context=HttpGet(clever_template_mapping+TemplateName);
        JSONArray jsonArray=JSON.parseObject(context).getJSONArray("data");
        for(int i=0;i<jsonArray.size();i++){
            NodeInfo nodeInfo=new NodeInfo();
            nodeInfo.setNodePath(jsonArray.getJSONObject(i).getString("nodePath"));
            nodeInfo.setEn_name(jsonArray.getJSONObject(i).getString("columnName"));
            nodeInfo.setCh_name(jsonArray.getJSONObject(i).getString("columnChName"));
            nodeInfo.setSpecialiseArchetypeId(specialiseArchetypeId);
            list.add(nodeInfo);
        }
        return list;
    }

    private String HttpGet(String url){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpget);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String result = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}

