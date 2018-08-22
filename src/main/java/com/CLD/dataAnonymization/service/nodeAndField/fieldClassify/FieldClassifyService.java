package com.CLD.dataAnonymization.service.nodeAndField.fieldClassify;

import com.CLD.dataAnonymization.dao.h2.entity.ArchetypeBasisFieldClassify;
import com.CLD.dataAnonymization.dao.h2.entity.ExpandBasisFieldClassify;
import com.CLD.dataAnonymization.model.FieldFormInfo;
import com.CLD.dataAnonymization.model.FieldInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 该类用于生成、操作可用匿名字段表
 * @Author CLD
 * @Date 2018/5/22 8:53
 **/
public interface FieldClassifyService {

    public List<String> createOrignalFrom();

    public List<String> createOrignalFrom(List<ArchetypeBasisFieldClassify> archetypeBasisFieldClassifyList,
                                          List<ExpandBasisFieldClassify> expandBasisFieldClassifyList);

    public List<String> getFromNameList();

    public List<String> getFromNameListByUserName(String userName);

    public List<FieldFormInfo> getFieldFormInfo();

    public FieldFormInfo getFieldFormInfoByFormName(String formName);

    public List<FieldInfo> getFieldByFromName(String fromName);

    public ArrayList<ArrayList<String>> getUseFieldByFromName(String fromName);

    public List<String> updataField(List<FieldInfo> fieldInfoList, String newFromName);

    public List<String> updateFieldFormInfo(List<FieldInfo> fieldInfoList, String newFormName,String oldFormName,String newDescription,String logDescription);

    public Boolean deleteFromByName(String fromName);

    public Boolean deleteFormByFormNameAndUserName(String formName,String userName);

    public Map<String,Double> getFieldOverViewByFormName(String formName);

    public Map<String,List<String>> getFieldDetailByFormName(String formName);

    public Boolean createFrom(String formName,String father,String userName,String description);

    /**
     * 该方法将文件形式的字段表，存入数据库
     * @return
     */
    public Boolean FileToDB();

    /**
     * 该方法将数据库形式的字段表保存为文件形式
     * @return
     */
    public Boolean DBToFile();

}
