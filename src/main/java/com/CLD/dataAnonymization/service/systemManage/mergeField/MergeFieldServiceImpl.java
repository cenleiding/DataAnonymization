package com.CLD.dataAnonymization.service.systemManage.mergeField;

import com.CLD.dataAnonymization.dao.h2.entity.FieldClassify;
import com.CLD.dataAnonymization.dao.h2.entity.FieldClassifyList;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyListRepository;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyRepository;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author CLD
 * @Date 2018/8/31 15:29
 */
@Service
public class MergeFieldServiceImpl implements MergeFieldService {

    @Autowired
    FieldClassifyRepository fieldClassifyRepository;

    @Autowired
    FieldClassifyListRepository fieldClassifyListRepository;

    @Value("${package.jar.name}")
    private String jarName;

    @Value("${field.out.path}")
    private String FieldOutPath;

    @Override
    public String mergeAllField() {
        String FilePath_mapping=new Object() {
            public String get(){
                return this.getClass().getClassLoader().getResource("").getPath();
            }
        }.get().replaceAll("target/classes/","")
                .replaceAll(jarName+"!/BOOT-INF/classes!/","")
                .replaceAll("file:","");
        List<FieldClassifyList> fieldClassifyListList =fieldClassifyListRepository.findByUserName("");
        JSONArray jsonArray=new JSONArray();
        String outList=new String();
        Map<String,String> fields=new HashMap<String,String>();
        for(FieldClassifyList fieldClassifyList:fieldClassifyListList){
            List<FieldClassify> fieldClassifyList1=fieldClassifyRepository.findByFormName(fieldClassifyList.getFormName());
            for(FieldClassify fieldClassify:fieldClassifyList1){
                if(fields.keySet().contains(fieldClassify.getFieldName())&& !fields.get(fieldClassify.getFieldName()).equals(fieldClassify.getFieldType())){
                    fields.put(fieldClassify.getFieldName(),"冲突");
                }
                if(!fields.keySet().contains(fieldClassify.getFieldName())){
                    fields.put(fieldClassify.getFieldName(),fieldClassify.getFieldType());
                }
            }
        }
        for(String key:fields.keySet()){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("fieldName",key);
            jsonObject.put("fieldType",fields.get(key));
            jsonArray.add(jsonObject);
        }
        String jsonStr =jsonArray.toJSONString();
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(FilePath_mapping+FieldOutPath+"/Original.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.print(jsonStr);
        pw.flush();
        pw.close();

        return outList;
    }
}
