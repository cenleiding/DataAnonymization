package com.CLD.dataAnonymization.service.nodeAndField.fieldClassify;

import com.CLD.dataAnonymization.model.FieldFormInfo;
import com.CLD.dataAnonymization.model.FieldInfo;

import java.util.List;
import java.util.Map;

/**
 * 该类用于生成、操作可用匿名字段表
 * @Author CLD
 * @Date 2018/5/22 8:53
 **/
public interface FieldClassifyService {

    //获得所有字段表单，详细信息
    public List<FieldFormInfo> getFieldFormInfo();

    //获得指定字段表单，详细信息
    public FieldFormInfo getFieldFormInfoByFormName(String formName);

    //获得指点字段表单信息
    public List<FieldInfo> getFieldByFromName(String formName);

    //更新指定字段表单，信息
    public List<String> updateFieldFormInfo(List<FieldInfo> fieldInfoList, String newFormName,String oldFormName,String newDescription,String logDescription);

    //根据用户名和表单名，删除表单
    public Boolean deleteFormByFormNameAndUserName(String formName,String userName);

    //获得指定表单，字段发布情况
    public Map<String,Double> getFieldOverViewByFormName(String formName);

    //获得指定表单，字段发布情况
    public Map<String,List<String>> getFieldDetailByFormName(String formName);

    //生成新的表单
    public Boolean createFrom(String formName,String father,String userName,String description);

    //该方法将文件形式的字段表，存入数据库
    public Boolean FileToDB();

    // 该方法将数据库形式的字段表保存为文件形式
    public Boolean DBToFile();

}
