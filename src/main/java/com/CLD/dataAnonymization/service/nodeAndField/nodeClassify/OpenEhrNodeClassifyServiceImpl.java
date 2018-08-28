package com.CLD.dataAnonymization.service.nodeAndField.nodeClassify;

import com.CLD.dataAnonymization.dao.h2.entity.ArchetypeNodeClassify;
import com.CLD.dataAnonymization.dao.h2.repository.ArchetypeNodeClassifyRepository;
import com.CLD.dataAnonymization.model.ArchetypeNodeInfo;
import com.CLD.dataAnonymization.service.nodeAndField.nodeToField.NodeToFieldService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author CLD
 * @Date 2018/5/22 10:43
 **/
@Service
public class OpenEhrNodeClassifyServiceImpl implements OpenEhrNodeClassifyService {

    @Value("${node.out.archetype.path}")
    private String archetypePath;

    @Value("${node.archetype.name}")
    private String archetypeName;

    @Value("${package.jar.name}")
    private String jarName;

    @Autowired
    ArchetypeNodeClassifyRepository archetypeNodeClassifyRepository;

    @Autowired
    NodeToFieldService nodeToFieldService;

    private String FilePath_mapping=new Object() {
        public String get(){
            return this.getClass().getClassLoader().getResource("").getPath();
        }
    }.get().replaceAll("target/classes/","")
            .replaceAll(jarName+"!/BOOT-INF/classes!/","")
            .replaceAll("file:","");

    @Override
    public Boolean FileToDB() {
        archetypeNodeClassifyRepository.deleteAll();
        InputStream is= null;
        JSONArray jsonArray=new JSONArray();
        try {
            is = new FileInputStream(FilePath_mapping+archetypePath+"/"+archetypeName);
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
        for(int i=0;i<jsonArray.size();i++){
            for(int j=0;j<jsonArray.getJSONObject(i).getJSONArray("fields").size();j++){
                ArchetypeNodeClassify archetypeNodeClassify =new ArchetypeNodeClassify();
                archetypeNodeClassify.setNodeType(jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("type"));
                archetypeNodeClassify.setNodePath(jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("nodePath"));
                archetypeNodeClassify.setNodeName(jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("nodeName"));
                archetypeNodeClassify.setDescription(jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("description"));
                archetypeNodeClassify.setArchetypeName(jsonArray.getJSONObject(i).getString("archetypeName"));
                archetypeNodeClassify.setArchetypeId(jsonArray.getJSONObject(i).getString("archetypeId"));
                archetypeNodeClassifyRepository.save(archetypeNodeClassify);
            }
        }

        return true;
    }

    @Override
    public Boolean DBToFile() {
        List<ArchetypeNodeClassify> archetypeNodeClassifyList = archetypeNodeClassifyRepository.findAll();
        Map<String,String> archetypeList=new HashMap<String,String>();
        for(ArchetypeNodeClassify archetypeNodeClassify : archetypeNodeClassifyList){
            archetypeList.put(archetypeNodeClassify.getArchetypeName(), archetypeNodeClassify.getArchetypeId());
        }
        JSONArray allJsonArray=new JSONArray();
        for(String key:archetypeList.keySet()){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("archetypeName",key);
            jsonObject.put("archetypeId",archetypeList.get(key));
            JSONArray jsonArray=new JSONArray();
            for(ArchetypeNodeClassify archetypeNodeClassify : archetypeNodeClassifyList){
                if(archetypeNodeClassify.getArchetypeName().equals(key)){
                    JSONObject jo=new JSONObject();
                    jo.put("nodeName", archetypeNodeClassify.getNodeName());
                    jo.put("nodePath", archetypeNodeClassify.getNodePath());
                    jo.put("type", archetypeNodeClassify.getNodeType());
                    jo.put("description", archetypeNodeClassify.getDescription());
                    jsonArray.add(jo);
                }
            }
            jsonObject.put("fields",jsonArray);
            allJsonArray.add(jsonObject);
        }
        String jsonStr =allJsonArray.toJSONString();
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(FilePath_mapping+archetypePath+"/backup"+archetypeName)));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        pw.print(jsonStr);
        pw.flush();
        pw.close();

        return true;
    }

    @Override
    public List<String> getArchetypeName() {
        List<String> archetypeNameList= archetypeNodeClassifyRepository.getArchetypeName();
        //对列表进行整理
        List<String> outList=new ArrayList<String>();
        for(String s:archetypeNameList) if(s.indexOf("openEHR-EHR-ACTION")!=-1) outList.add(s);
        for(String s:archetypeNameList) if(s.indexOf("openEHR-EHR-EVALUATION")!=-1) outList.add(s);
        for(String s:archetypeNameList) if(s.indexOf("openEHR-EHR-OBSERVATION")!=-1) outList.add(s);
        for(String s:archetypeNameList) if(s.indexOf("openEHR-EHR-INSTRUCTION")!=-1) outList.add(s);
        for(String s:archetypeNameList) if(s.indexOf("openEHR-EHR-ADMIN_ENTRY")!=-1) outList.add(s);
        for(String s:archetypeNameList) if(s.indexOf("openEHR-EHR-CLUSTER")!=-1) outList.add(s);
        for(String s:archetypeNameList) if(!outList.contains(s)) outList.add(s);
        return outList;
    }

    @Override
    public List<ArchetypeNodeInfo> getArchetypeNodeInfoByName(String archetypeName) {
        List<ArchetypeNodeClassify> archetypeNodeClassifyList = archetypeNodeClassifyRepository.findByArchetypeName(archetypeName);
        List<ArchetypeNodeInfo> archetypeNodeInfoList=new ArrayList<ArchetypeNodeInfo>();
        for(ArchetypeNodeClassify archetypeNodeClassify : archetypeNodeClassifyList){
            ArchetypeNodeInfo archetypeNodeInfo=new ArchetypeNodeInfo();
            archetypeNodeInfo.setArchetypeId(archetypeNodeClassify.getArchetypeId());
            archetypeNodeInfo.setArchetypeName(archetypeNodeClassify.getArchetypeName());
            archetypeNodeInfo.setDescription(archetypeNodeClassify.getDescription());
            archetypeNodeInfo.setNodeName(archetypeNodeClassify.getNodeName());
            archetypeNodeInfo.setNodePath(archetypeNodeClassify.getNodePath());
            archetypeNodeInfo.setNodeType(archetypeNodeClassify.getNodeType());
            archetypeNodeInfo.setId(String.valueOf(archetypeNodeClassify.getId()));
            archetypeNodeInfoList.add(archetypeNodeInfo);
        }
        return archetypeNodeInfoList;
    }

}
