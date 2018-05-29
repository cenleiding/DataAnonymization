package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.dao.h2.entity.FieldClassify;
import com.CLD.dataAnonymization.dao.h2.entity.NodeClassify;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyRepository;
import com.CLD.dataAnonymization.dao.h2.repository.NodeClassifyRepository;
import com.CLD.dataAnonymization.model.TemplateNodeInfo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author CLD
 * @Date 2018/5/22 9:00
 **/
@Service
public class NodeToFieldServiceImpl implements NodeToFieldService {

    @Value("${clever.template.deploy}")
    private String clever_template_deploy;
//    ="http://172.16.100.63:8000/clever-rest/template/deploy/record/latest";

    @Value("${clever.template.mapping}")
    private String clever_template_mapping;
//    ="http://172.16.100.63:8000/clever-rest/template/mapping/";

    @Autowired
    FieldClassifyRepository fieldClassifyRepository;

    @Autowired
    NodeClassifyRepository nodeClassifyRepository;

    @Override
    public List<String> NodeToField(List<NodeClassify> nodeClassifyList) {
        List<String> outList=new ArrayList<String>();
        Map<String,String> nodeMap=new HashMap<String,String>();
        Map<String,String> fieldMap=new HashMap<String,String>();
        //获取节点分类信息
        for(NodeClassify nodeClassify:nodeClassifyList){
            nodeMap.put(nodeClassify.getArchetypeId()+nodeClassify.getNodePath(),nodeClassify.getNodeType());
        }
        //获取模板信息
        List<String[]> templateNames=GetTemplateName();
        List<TemplateNodeInfo> nodeList=new ArrayList<TemplateNodeInfo>();
        for(String[] name:templateNames){
            nodeList.addAll(GetTemplateMapping(name[0],name[1]));
        }
        //进行字段分类
        for(int i=0;i<nodeList.size();i++) {
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
            if(nodeMap.get(pathAndId)!=null){
                if(nodeMap.get(pathAndId).equals("EI"))
                    System.out.println(pathAndId);
                if(db_field!=null&&!db_field.equals("")){
                    if(fieldMap.keySet().contains(db_field)&&!fieldMap.get(db_field).equals(nodeMap.get(pathAndId)))
                       outList.add("节点"+pathAndId+"敏感程度冲突！");
                        fieldMap.put(db_field,nodeMap.get(pathAndId));
                }
                if(en_field!=null&&!en_field.equals("")){
                    if(fieldMap.keySet().contains(en_field)&&!fieldMap.get(en_field).equals(nodeMap.get(pathAndId)))
                        outList.add("节点"+pathAndId+"敏感程度冲突！");
                    fieldMap.put(en_field,nodeMap.get(pathAndId));
                }
                if(ch_field!=null&&!ch_field.equals("")){
                    if(fieldMap.keySet().contains(ch_field)&&!fieldMap.get(ch_field).equals(nodeMap.get(pathAndId)))
                        outList.add("节点"+pathAndId+"敏感程度冲突！");
                    fieldMap.put(ch_field,nodeMap.get(pathAndId));
                }
            }

        }
        if(outList.size()!=0) return outList;
        //存储字段分类
        fieldClassifyRepository.deleteByFromName("Original");
        for (String key:fieldMap.keySet()){
            if(fieldMap.get(key)!=null && !fieldMap.get(key).equals("NI")){
                FieldClassify fieldClassify=new FieldClassify();
                fieldClassify.setFromName("Original");
                fieldClassify.setFieldType(fieldMap.get(key));
                fieldClassify.setFieldName(key);
                fieldClassifyRepository.save(fieldClassify);
            }
        }
        outList.add("Original字段表更新成功");
        return outList;
    }

    @Override
    public List<String> NodeToField() {
        List<NodeClassify> nodeClassifyList=nodeClassifyRepository.findAll();
        return NodeToField(nodeClassifyList);
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

    private List<TemplateNodeInfo> GetTemplateMapping(String TemplateName, String specialiseArchetypeId){
        List<TemplateNodeInfo> list=new ArrayList<TemplateNodeInfo>();
        String context=HttpGet(clever_template_mapping+TemplateName);
        JSONArray jsonArray= JSON.parseObject(context).getJSONArray("data");
        for(int i=0;i<jsonArray.size();i++){
            TemplateNodeInfo templateNodeInfo =new TemplateNodeInfo();
            templateNodeInfo.setNodePath(jsonArray.getJSONObject(i).getString("nodePath"));
            templateNodeInfo.setEn_name(jsonArray.getJSONObject(i).getString("columnName"));
            templateNodeInfo.setCh_name(jsonArray.getJSONObject(i).getString("columnChName"));
            templateNodeInfo.setSpecialiseArchetypeId(specialiseArchetypeId);
            list.add(templateNodeInfo);
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
