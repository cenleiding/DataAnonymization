package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.dao.h2.entity.FieldClassify;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyRepository;
import com.CLD.dataAnonymization.model.FieldInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author CLD
 * @Date 2018/5/23 9:08
 **/
@Service
public class FieldClassifyServiceImpl implements FieldClassifyService {

    @Autowired
    FieldClassifyRepository fieldClassifyRepository;

    @Override
    public List<String> getFromNameList() {
        return fieldClassifyRepository.getFromName();
    }

    @Override
    public List<FieldInfo> getFieldByFromName(String fromName) {
        List<FieldClassify> fieldClassifyList=fieldClassifyRepository.findByFromName(fromName);
        List<FieldInfo> fieldInfoList=new ArrayList<FieldInfo>();
        for(FieldClassify fieldClassify:fieldClassifyList){
            FieldInfo fieldInfo=new FieldInfo();
            fieldInfo.setFieldName(fieldClassify.getFieldName());
            fieldInfo.setFieldType(fieldClassify.getFieldType());
            fieldInfo.setFromName(fieldClassify.getFromName());
            fieldInfo.setId(String.valueOf(fieldClassify.getID()));
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
        List<String> fromNameList=fieldClassifyRepository.getFromName();
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
        fieldClassifyRepository.deleteByFromName(fromName);
        for(FieldInfo fieldInfo:fieldInfoList){
            if(fieldInfo.getFieldName()==null||fieldInfo.getFieldType()==null) continue;
            FieldClassify fieldClassify=new FieldClassify();
            fieldClassify.setFromName(newFromName);
            fieldClassify.setFieldName(fieldInfo.getFieldName());
            fieldClassify.setFieldType(fieldInfo.getFieldType());
            fieldClassifyRepository.save(fieldClassify);
        }
        outList.add("更新成功！");
        return outList;
    }

    @Override
    public Boolean deleteFromByName(String fromName) {
        fieldClassifyRepository.deleteByFromName(fromName);
        return true;
    }


}
