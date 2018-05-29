package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.model.FieldInfo;

import java.util.List;

/**
 * 该类用于操作匿名字段表
 * @Author CLD
 * @Date 2018/5/22 8:53
 **/
public interface FieldClassifyService {

    public List<String> getFromNameList();

    public List<FieldInfo> getFieldByFromName(String fromName);

    public List<String> updataField(List<FieldInfo> fieldInfoList, String newFromName);

    public Boolean deleteFromByName(String fromName);

}
