package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.dao.h2.entity.NodeClassify;

import java.util.List;

/**
 * 该类用于将节点分类表转换为字段分类表
 * @Author CLD
 * @Date 2018/5/22 8:56
 **/
public interface NodeToFieldService {

    public List<String> NodeToField();

    public List<String> NodeToField(List<NodeClassify> nodeClassifyList);
}
