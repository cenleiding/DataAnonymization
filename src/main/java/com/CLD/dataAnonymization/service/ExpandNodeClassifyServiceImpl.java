package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.dao.h2.entity.ExpandNodeClassify;
import com.CLD.dataAnonymization.dao.h2.repository.ExpandNodeClassifyRepository;
import com.CLD.dataAnonymization.model.ExpandNodeInfo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author CLD
 * @Date 2018/6/4 9:17
 **/
@Service
public class ExpandNodeClassifyServiceImpl implements ExpandNodeClassifyService {

    @Autowired
    ExpandNodeClassifyRepository expandNodeClassifyRepository;

    @Autowired
    NodeToFieldService nodeToFieldService;

    private static final String FilePath_mapping=new Object() {
        public String get(){
            return this.getClass().getClassLoader().getResource("").getPath();
        }
    }.get().replaceAll("target/classes/","")
            .replaceAll("1.jar!/BOOT-INF/classes!/","")
            .replaceAll("file:","")+"resources/expand";

    @Override
    public Boolean FileToDB() {
        expandNodeClassifyRepository.deleteAll();
        InputStream is= null;
        File file=new File(FilePath_mapping);
        String[] fileList = file.list();
        for (int i = 0; i < fileList.length; i++) {
            JSONArray jsonArray=new JSONArray();
            String path=FilePath_mapping+"/"+fileList[i];
            try {
                is = new FileInputStream(path);
                JSONReader reader=new JSONReader(new InputStreamReader(is,"UTF-8"));
                reader.startArray();
                while(reader.hasNext()) {
                    JSONObject ja= (JSONObject) reader.readObject();
                    jsonArray.add(ja);
                }
                reader.endArray();
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return false;
            }
            for(int j=0;j<jsonArray.size();j++){
                for(int k=0;k<jsonArray.getJSONObject(j).getJSONArray("fields").size();k++) {
                    ExpandNodeClassify expandNodeClassify=new ExpandNodeClassify();
                    expandNodeClassify.setExpandName(fileList[i]);
                    expandNodeClassify.setFromName(jsonArray.getJSONObject(j).getString("fromName"));
                    expandNodeClassify.setNodeName(jsonArray.getJSONObject(j).getJSONArray("fields").getJSONObject(k).getString("nodeName"));
                    expandNodeClassify.setNodeType(jsonArray.getJSONObject(j).getJSONArray("fields").getJSONObject(k).getString("type"));
                    expandNodeClassifyRepository.save(expandNodeClassify);
                }
            }
        }
        return true;
    }

    @Override
    public Boolean DBToFile() {
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
                        jo.put("nodeName",expandNodeClassify.getNodeName());
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
                pw = new PrintWriter(new BufferedWriter(new FileWriter(FilePath_mapping+"/"+expandName)));
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
            expandNodeInfo.setNodeName(expandNodeClassify.getNodeName());
            expandNodeInfo.setNodeType(expandNodeClassify.getNodeType());
            expandNodeInfoList.add(expandNodeInfo);
        }
        return expandNodeInfoList;
    }

    @Override
    public List<String> updataNodeInfo(List<ExpandNodeInfo> expandNodeInfoList, String fileName, String fromName) {
        List<String> outList=new ArrayList<String>();
        if(fileName ==null || fileName.equals("") || fromName ==null || fromName.equals(""))return null;
        List<ExpandNodeClassify> expandNodeClassifyList=expandNodeClassifyRepository.findByExpandNameIsNotOrFromNameIsNot(fileName,fromName);
        for(ExpandNodeInfo expandNodeInfo:expandNodeInfoList){
            ExpandNodeClassify expandNodeClassify=new ExpandNodeClassify();
            expandNodeClassify.setNodeType(expandNodeInfo.getNodeType());
            expandNodeClassify.setNodeName(expandNodeInfo.getNodeName());
            expandNodeClassify.setFromName(expandNodeInfo.getFromName());
            expandNodeClassify.setExpandName(expandNodeInfo.getExpandName());
            expandNodeClassifyList.add(expandNodeClassify);
        }
        //进入NodeToField模块
        outList=nodeToFieldService.ExpandNodeToField(expandNodeClassifyList);
        if(outList.size()!=0) return outList;

        //保存节点更改
        expandNodeClassifyRepository.deleteAll();
        expandNodeClassifyRepository.saveAll(expandNodeClassifyList);

        return outList;
    }
}