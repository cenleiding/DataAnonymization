package com.CLD.dataAnonymization.service.nodeAndField.nodeClassify;

import com.CLD.dataAnonymization.dao.h2.entity.ExpandNodeClassify;
import com.CLD.dataAnonymization.dao.h2.repository.ExpandNodeClassifyRepository;
import com.CLD.dataAnonymization.model.ExpandNodeInfo;
import com.CLD.dataAnonymization.service.nodeAndField.nodeToField.NodeToFieldService;
import com.CLD.dataAnonymization.util.deidentifier.io.FileResolve;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:该方法用于操作拓展节点分类表
 * @Author CLD
 * @Date 2018/6/4 9:17
 **/
@Service
public class ExpandNodeClassifyServiceImpl implements ExpandNodeClassifyService {

    @Value("${node.out.expand.path}")
    private String expandPath;

    @Value("${package.jar.name}")
    private String jarName;

    @Autowired
    ExpandNodeClassifyRepository expandNodeClassifyRepository;

    @Autowired
    NodeToFieldService nodeToFieldService;

    @Override
    public Boolean FileToDB() {
        String FilePath_mapping=new Object() {
            public String get(){
                return this.getClass().getClassLoader().getResource("").getPath();
            }
        }.get().replaceAll("target/classes/","")
                .replaceAll(jarName+"!/BOOT-INF/classes!/","")
                .replaceAll("file:","");
        expandNodeClassifyRepository.deleteAll();
        expandNodeClassifyRepository.flush();
        File file=new File(FilePath_mapping+expandPath);
        String[] fileList = file.list();
        for (int i = 0; i < fileList.length; i++) {
            JSONArray jsonArray=new JSONArray();
            String path=FilePath_mapping+expandPath+"/"+fileList[i];
            try {
                jsonArray= FileResolve.readerArrayJson(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            List<ExpandNodeClassify> expandNodeClassifyList=new ArrayList<ExpandNodeClassify>();
            for(int j=0;j<jsonArray.size();j++){
                for(int k=0;k<jsonArray.getJSONObject(j).getJSONArray("fields").size();k++) {
                    ExpandNodeClassify expandNodeClassify=new ExpandNodeClassify();
                    expandNodeClassify.setExpandName(fileList[i]);
                    expandNodeClassify.setFromName(jsonArray.getJSONObject(j).getString("fromName"));
                    expandNodeClassify.setCH_name(jsonArray.getJSONObject(j).getJSONArray("fields").getJSONObject(k).getString("CH_Name"));
                    expandNodeClassify.setEN_name(jsonArray.getJSONObject(j).getJSONArray("fields").getJSONObject(k).getString("EN_Name"));
                    expandNodeClassify.setDescription(jsonArray.getJSONObject(j).getJSONArray("fields").getJSONObject(k).getString("description"));
                    expandNodeClassify.setNodeType(jsonArray.getJSONObject(j).getJSONArray("fields").getJSONObject(k).getString("type"));
                    expandNodeClassifyList.add(expandNodeClassify);
                }
            }
            expandNodeClassifyRepository.saveAll(expandNodeClassifyList);
        }
        return true;
    }

    @Override
    public Boolean DBToFile() {
        String FilePath_mapping=new Object() {
            public String get(){
                return this.getClass().getClassLoader().getResource("").getPath();
            }
        }.get().replaceAll("target/classes/","")
                .replaceAll(jarName+"!/BOOT-INF/classes!/","")
                .replaceAll("file:","");
        List<ExpandNodeClassify> expandNodeClassifyList=expandNodeClassifyRepository.findAll();
        List<String> expandNameList=expandNodeClassifyRepository.getExpandName();
        for(String expandName:expandNameList){
            List<String> fromNameList=expandNodeClassifyRepository.getFromNameByExpandName(expandName);
            JSONArray allJsonArray=new JSONArray();
            for(String fromName:fromNameList){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("fromName",fromName);
                JSONArray jsonArray=new JSONArray();
                for(ExpandNodeClassify expandNodeClassify:expandNodeClassifyList){
                    if(expandNodeClassify.getExpandName().equals(expandName)&&expandNodeClassify.getFromName().equals(fromName)){
                        JSONObject jo=new JSONObject();
                        jo.put("EN_Name",expandNodeClassify.getEN_name());
                        jo.put("CH_Name",expandNodeClassify.getCH_name());
                        jo.put("description",expandNodeClassify.getDescription());
                        jo.put("type",expandNodeClassify.getNodeType());
                        jsonArray.add(jo);
                    }
                }
                jsonObject.put("fields",jsonArray);
                allJsonArray.add(jsonObject);
            }
            String jsonStr =allJsonArray.toJSONString();
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(new BufferedWriter(new FileWriter(FilePath_mapping+expandPath+"/backup"+expandName)));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            pw.print(jsonStr);
            pw.flush();
            pw.close();

        }
        return true;
    }

    @Override
    public List<String> getFileName() {
        return expandNodeClassifyRepository.getExpandName();
    }

    @Override
    public List<String> getFromNameByFileName(String FileName) {
        return expandNodeClassifyRepository.getFromNameByExpandName(FileName);
    }

    @Override
    public List<ExpandNodeInfo> getNodeInfoByName(String fileName, String fromName) {
        List<ExpandNodeClassify> expandNodeClassifyList=expandNodeClassifyRepository.findByExpandNameAndFromName(fileName,fromName);
        List<ExpandNodeInfo> expandNodeInfoList=new ArrayList<ExpandNodeInfo>();
        for(ExpandNodeClassify expandNodeClassify:expandNodeClassifyList){
            ExpandNodeInfo expandNodeInfo=new ExpandNodeInfo();
            expandNodeInfo.setExpandName(expandNodeClassify.getExpandName());
            expandNodeInfo.setFromName(expandNodeClassify.getFromName());
            expandNodeInfo.setId(expandNodeClassify.getId());
            expandNodeInfo.setEN_name(expandNodeClassify.getEN_name());
            expandNodeInfo.setCH_name(expandNodeClassify.getCH_name());
            expandNodeInfo.setDescription(expandNodeClassify.getDescription());
            expandNodeInfo.setNodeType(expandNodeClassify.getNodeType());
            expandNodeInfoList.add(expandNodeInfo);
        }
        return expandNodeInfoList;
    }

}
