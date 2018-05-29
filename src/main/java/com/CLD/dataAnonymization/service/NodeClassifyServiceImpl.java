package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.dao.h2.entity.NodeClassify;
import com.CLD.dataAnonymization.dao.h2.repository.NodeClassifyRepository;
import com.CLD.dataAnonymization.model.ArchetypeNodeInfo;
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
 * @Date 2018/5/22 10:43
 **/
@Service
public class NodeClassifyServiceImpl implements NodeClassifyService {

    @Autowired
    NodeClassifyRepository nodeClassifyRepository;

    @Autowired
    NodeToFieldService nodeToFieldService;

    private static final String FilePath_mapping=new Object() {
        public String get(){
            return this.getClass().getClassLoader().getResource("").getPath();
        }
    }.get().replaceAll("target/classes/","")
            .replaceAll("1.jar!/BOOT-INF/classes!/","")
            .replaceAll("file:","")+"resources/Form_mapping.json";

    @Override
    public Boolean FileToDB() {
        nodeClassifyRepository.deleteAll();
        InputStream is= null;
        JSONArray jsonArray=new JSONArray();
        try {
            is = new FileInputStream(FilePath_mapping);
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
                NodeClassify nodeClassify=new NodeClassify();
                nodeClassify.setNodeType(jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("type"));
                nodeClassify.setNodePath(jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("nodePath"));
                nodeClassify.setNodeName(jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("nodeName"));
                nodeClassify.setDescription(jsonArray.getJSONObject(i).getJSONArray("fields").getJSONObject(j).getString("description"));
                nodeClassify.setArchetypeName(jsonArray.getJSONObject(i).getString("archetypeName"));
                nodeClassify.setArchetypeId(jsonArray.getJSONObject(i).getString("archetypeId"));
                nodeClassifyRepository.save(nodeClassify);
            }
        }

        return true;
    }

    @Override
    public List<String> getArchetypeName() {
        List<String> archetypeNameList=nodeClassifyRepository.getArchetypeName();
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
        List<NodeClassify> nodeClassifyList=nodeClassifyRepository.findByArchetypeName(archetypeName);
        List<ArchetypeNodeInfo> archetypeNodeInfoList=new ArrayList<ArchetypeNodeInfo>();
        for(NodeClassify nodeClassify:nodeClassifyList){
            ArchetypeNodeInfo archetypeNodeInfo=new ArchetypeNodeInfo();
            archetypeNodeInfo.setArchetypeId(nodeClassify.getArchetypeId());
            archetypeNodeInfo.setArchetypeName(nodeClassify.getArchetypeName());
            archetypeNodeInfo.setDescription(nodeClassify.getDescription());
            archetypeNodeInfo.setNodeName(nodeClassify.getNodeName());
            archetypeNodeInfo.setNodePath(nodeClassify.getNodePath());
            archetypeNodeInfo.setNodeType(nodeClassify.getNodeType());
            archetypeNodeInfo.setId(String.valueOf(nodeClassify.getID()));
            archetypeNodeInfoList.add(archetypeNodeInfo);
        }
        return archetypeNodeInfoList;
    }

    @Override
    public List<String> updataNodeInfo(List<ArchetypeNodeInfo> archetypeNodeInfoList,String archetypeName) {
        List<String> outList=new ArrayList<String>();
        if(archetypeNodeInfoList ==null || archetypeName.equals("")) return null;
        List<NodeClassify> nodeClassifyList=nodeClassifyRepository.findByArchetypeNameIsNot(archetypeName);

        for(ArchetypeNodeInfo archetypeNodeInfo:archetypeNodeInfoList){
            NodeClassify nodeClassify=new NodeClassify();
            nodeClassify.setArchetypeName(archetypeNodeInfo.getArchetypeName());
            nodeClassify.setArchetypeId(archetypeNodeInfo.getArchetypeId());
            nodeClassify.setDescription(archetypeNodeInfo.getDescription());
            nodeClassify.setNodeName(archetypeNodeInfo.getNodeName());
            nodeClassify.setNodePath(archetypeNodeInfo.getNodePath());
            nodeClassify.setNodeType(archetypeNodeInfo.getNodeType());
            nodeClassifyList.add(nodeClassify);
        }
        outList=nodeToFieldService.NodeToField(nodeClassifyList);
        if(outList.get(0).equals("Original字段表更新成功")){
            nodeClassifyRepository.deleteAll();
            nodeClassifyRepository.saveAll(nodeClassifyList);
            outList.add("节点字段表更新成功");
        }
        return outList;
    }

    @Override
    public List<Double> getNodeOverView() {
        List<NodeClassify> nodeClassifyList=nodeClassifyRepository.findAll();
        List<Double> overView=new ArrayList<Double>();
        Double EI= Double.valueOf(0);
        Double QI_Geographic= Double.valueOf(0);
        Double QI_Date= Double.valueOf(0);
        Double QI_Number= Double.valueOf(0);
        Double QI_String= Double.valueOf(0);
        Double SI_Number= Double.valueOf(0);
        Double SI_String= Double.valueOf(0);
        Double UI=Double.valueOf(0);
        Double sum= Double.valueOf(0);
        for(NodeClassify nodeClassify:nodeClassifyList){
            if(nodeClassify.getNodeType().equals("EI")) EI++;
            if(nodeClassify.getNodeType().equals("QI_Geographic")) QI_Geographic++;
            if(nodeClassify.getNodeType().equals("QI_Date")) QI_Date++;
            if(nodeClassify.getNodeType().equals("QI_Number")) QI_Number++;
            if(nodeClassify.getNodeType().equals("QI_String")) QI_String++;
            if(nodeClassify.getNodeType().equals("SI_Number")) SI_Number++;
            if(nodeClassify.getNodeType().equals("SI_String")) SI_String++;
            if(nodeClassify.getNodeType().equals("UI")) UI++;
        }
        sum=EI+QI_Geographic+QI_Date+QI_Number+QI_String+SI_Number+SI_String+UI;
        overView.add((double) (EI/sum));
        overView.add((double) (QI_Geographic/sum));
        overView.add((double) (QI_Date/sum));
        overView.add((double) (QI_Number/sum));
        overView.add((double) (QI_String/sum));
        overView.add((double) (SI_Number/sum));
        overView.add((double) (SI_String/sum));
        overView.add((double) (UI/sum));
        overView.add(sum);
        return overView;
    }
}
