package com.CLD.dataAnonymization.service.nodeAndField.fieldClassify;

import com.CLD.dataAnonymization.dao.h2.entity.ArchetypeBasisFieldClassify;
import com.CLD.dataAnonymization.dao.h2.entity.ExpandBasisFieldClassify;
import com.CLD.dataAnonymization.dao.h2.entity.FieldClassifyList;
import com.CLD.dataAnonymization.dao.h2.entity.UsageFieldClassify;
import com.CLD.dataAnonymization.dao.h2.repository.*;
import com.CLD.dataAnonymization.model.FieldFormMap;
import com.CLD.dataAnonymization.model.FieldInfo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * @Author CLD
 * @Date 2018/5/23 9:08
 **/
@Service
public class FieldClassifyServiceImpl implements FieldClassifyService {

    @Value("${field.out.path}")
    private String fieldPath;

    @Value("${package.jar.name}")
    private String jarName;

    private String FilePath_mapping=new Object() {
        public String get(){
            return this.getClass().getClassLoader().getResource("").getPath();
        }
    }.get().replaceAll("target/classes/","")
            .replaceAll(jarName+"!/BOOT-INF/classes!/","")
            .replaceAll("file:","");

    @Autowired
    UsageFieldClassifyRepository usageFieldClassifyRepository;

    @Autowired
    ArchetypeBasisFieldClassifyRepository archetypeBasisFieldClassifyRepository;

    @Autowired
    ExpandBasisFieldClassifyRepository expandBasisFieldClassifyRepository;

    @Autowired
    FieldClassifyListRepository fieldClassifyListRepository;

    @Autowired
    FieldClassifyRepository fieldClassifyRepository;

    @Override
    public List<String> createOrignalFrom() {
        return createOrignalFrom(archetypeBasisFieldClassifyRepository.findAll(),expandBasisFieldClassifyRepository.findAll());
    }


    @Override
    public List<String> createOrignalFrom(List<ArchetypeBasisFieldClassify> archetypeBasisFieldClassifyList,
                                          List<ExpandBasisFieldClassify> expandBasisFieldClassifyList) {
        if(archetypeBasisFieldClassifyList==null) archetypeBasisFieldClassifyList=archetypeBasisFieldClassifyRepository.findAll();
        if(expandBasisFieldClassifyList==null) expandBasisFieldClassifyList=expandBasisFieldClassifyRepository.findAll();
        Map<String,String> fieldMap=new HashMap<String, String>();
        Map<String,String> pathMap=new HashMap<String, String>();
        List<String> outList=new ArrayList<String>();
        for(ArchetypeBasisFieldClassify archetypeBasisFieldClassify:archetypeBasisFieldClassifyList){
            String fieldName=archetypeBasisFieldClassify.getFieldName();
            String fieldType=archetypeBasisFieldClassify.getFieldType();
            String fieldPath=archetypeBasisFieldClassify.getArchetypePath();
            if((fieldMap.get(fieldName)!=null)&& (!fieldMap.get(fieldName).equals(fieldType)))
                outList.add("表："+pathMap.get(fieldName)+";"+fieldPath+"字段："+fieldName+"冲突！");
                fieldMap.put(fieldName,fieldType);
                pathMap.put(fieldName,pathMap.get(fieldName)==null?fieldPath:pathMap.get(fieldName)+";"+fieldPath);
        }
        for(ExpandBasisFieldClassify expandBasisFieldClassify:expandBasisFieldClassifyList){
            String fieldName=expandBasisFieldClassify.getFieldName();
            String fieldType=expandBasisFieldClassify.getFieldType();
            String fieldPath=expandBasisFieldClassify.getExpandFromName();
            if((fieldMap.get(fieldName)!=null)&& (!fieldMap.get(fieldName).equals(fieldType)))
                outList.add("表："+pathMap.get(fieldName)+";"+fieldPath+"字段："+fieldName+"冲突！");
            fieldMap.put(fieldName,fieldType);
            pathMap.put(fieldName,pathMap.get(fieldName)==null?fieldPath:pathMap.get(fieldName)+";"+fieldPath);
        }
        if(outList.size()!=0) return outList;

        //保存original表
        usageFieldClassifyRepository.deleteByFromName("Original");
        for(String key:fieldMap.keySet()){
            UsageFieldClassify usageFieldClassify=new UsageFieldClassify();
            usageFieldClassify.setFieldName(key);
            usageFieldClassify.setFieldType(fieldMap.get(key));
            usageFieldClassify.setFromName("Original");
            usageFieldClassifyRepository.save(usageFieldClassify);
        }

        return outList;
    }

    @Override
    public List<String> getFromNameList() {
        return usageFieldClassifyRepository.getFromName();
    }

    @Override
    public List<FieldFormMap> getFromNameMap(){
        List<FieldFormMap> fieldFormMapList=new ArrayList<FieldFormMap>();
        List<String> userNameList=fieldClassifyListRepository.getUserName();
        for(String userName:userNameList){
            FieldFormMap fieldFormMap=new FieldFormMap();
            fieldFormMap.setUserName(userName);
            Map<String,String> map=new HashMap<String,String>();
            List<FieldClassifyList> fieldClassifyListList=fieldClassifyListRepository.findByUserName(userName);
            for(FieldClassifyList fieldClassifyList:fieldClassifyListList){
                map.put(fieldClassifyList.getFormName(),fieldClassifyList.getDescription());
            }
            fieldFormMap.setFormNameAndDes(map);
            fieldFormMapList.add(fieldFormMap);
        }
        return fieldFormMapList;
    }


    @Override
    public List<FieldInfo> getFieldByFromName(String fromName) {
        List<UsageFieldClassify> usageFieldClassifyList = usageFieldClassifyRepository.findByFromName(fromName);
        List<FieldInfo> fieldInfoList=new ArrayList<FieldInfo>();
        for(UsageFieldClassify usageFieldClassify : usageFieldClassifyList){
            FieldInfo fieldInfo=new FieldInfo();
            fieldInfo.setFieldName(usageFieldClassify.getFieldName());
            fieldInfo.setFieldType(usageFieldClassify.getFieldType());
            fieldInfo.setFromName(usageFieldClassify.getFromName());
            fieldInfo.setId(String.valueOf(usageFieldClassify.getID()));
            fieldInfoList.add(fieldInfo);
        }
        return fieldInfoList;
    }

    @Override
    public ArrayList<ArrayList<String>> getUseFieldByFromName(String fromName) {
        List<FieldInfo> fieldInfoList=getFieldByFromName(fromName);
        ArrayList<ArrayList<String>> fieldList=new ArrayList<ArrayList<String>>();
        for(FieldInfo fieldInfo:fieldInfoList){
            ArrayList<String> field=new ArrayList<String>();
            field.add(fieldInfo.getFieldName());
            field.add(fieldInfo.getFieldType());
            fieldList.add(field);
        }
        return fieldList;
    }

    @Override
    public List<String> updataField(List<FieldInfo> fieldInfoList, String newFromName) {
        List<String> outList=new ArrayList<String>();
        if(fieldInfoList.get(0).getFieldType()==null){
            outList.add("请指定类型");
            return outList;
        }
        if(fieldInfoList==null ||fieldInfoList.get(0).getFromName().equals("Original")) {
            outList.add("Original表无法修改");
            return outList;
        }
        List<String> fromNameList= usageFieldClassifyRepository.getFromName();
        if(!fieldInfoList.get(0).getFromName().equals(newFromName)&&fromNameList.contains(newFromName)){
            outList.add(newFromName+"表名重复!");
            return outList;
        }
        if(newFromName.equals("null") || newFromName.replace(" ","").equals("")){
            outList.add("表名不能为空");
            return outList;
        }
        //检验字段是否可行(不能有重复字段)
        Set<String> fields=new HashSet<String>();
        for(FieldInfo fieldInfo:fieldInfoList){
            if(fields.contains(fieldInfo.getFieldName())) outList.add("字段"+fieldInfo.getFieldName()+"冲突/r/n");
            else fields.add(fieldInfo.getFieldName());
        }
        if(outList.size()!=0) return outList;
        //
        String fromName=fieldInfoList.get(0).getFromName();
        usageFieldClassifyRepository.deleteByFromName(fromName);
        for(FieldInfo fieldInfo:fieldInfoList){
            if(fieldInfo.getFieldName()==null||fieldInfo.getFieldType()==null) continue;
            UsageFieldClassify usageFieldClassify =new UsageFieldClassify();
            usageFieldClassify.setFromName(newFromName);
            usageFieldClassify.setFieldName(fieldInfo.getFieldName());
            usageFieldClassify.setFieldType(fieldInfo.getFieldType());
            usageFieldClassifyRepository.save(usageFieldClassify);
        }
        outList.add("更新成功！");
        return outList;
    }

    @Override
    public Boolean deleteFromByName(String formName) {
        usageFieldClassifyRepository.deleteByFromName(formName);
        fieldClassifyListRepository.deleteByFormName(formName);
        fieldClassifyRepository.deleteByFromName(formName);
        return true;
    }

    @Override
    public Boolean FileToDB() {
        InputStream is= null;
        File file=new File(FilePath_mapping+fieldPath);
        String[] fileList = file.list();
        for (int i = 0; i < fileList.length; i++) {
            if(fileList[i].equals("Original.json")) continue;
            usageFieldClassifyRepository.deleteByFromName(fileList[i].split("\\.")[0]);
            JSONArray jsonArray=new JSONArray();
            String path=FilePath_mapping+fieldPath+"/"+fileList[i];
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
                UsageFieldClassify usageFieldClassify=new UsageFieldClassify();
                usageFieldClassify.setFromName(fileList[i].split("\\.")[0]);
                usageFieldClassify.setFieldName(jsonArray.getJSONObject(j).getString("fieldName"));
                usageFieldClassify.setFieldType(jsonArray.getJSONObject(j).getString("fieldType"));
                usageFieldClassifyRepository.save(usageFieldClassify);
            }
        }
        return true;
    }

    @Override
    public Boolean DBToFile() {
        List<String> fieldNameList=usageFieldClassifyRepository.getFromName();
        for(String fieldName:fieldNameList) {
            List<UsageFieldClassify> usageFieldClassifyList = usageFieldClassifyRepository.findByFromName(fieldName);
            JSONArray jsonArray=new JSONArray();
            for(UsageFieldClassify usageFieldClassify:usageFieldClassifyList){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("fieldName",usageFieldClassify.getFieldName());
                jsonObject.put("fieldType",usageFieldClassify.getFieldType());
                jsonArray.add(jsonObject);
            }
            String jsonStr =jsonArray.toJSONString();
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(new BufferedWriter(new FileWriter(FilePath_mapping+fieldPath+"/backup"+fieldName+".json")));
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


}
