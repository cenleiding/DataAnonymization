package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.dao.h2.entity.ArchetypeNodeClassify;
import com.CLD.dataAnonymization.dao.h2.entity.ExpandNodeClassify;

import java.util.List;

/**
 * 该类用于将节点分类表转换为字段分类表
 * @Author CLD
 * @Date 2018/5/22 8:56
 **/
public interface NodeToFieldService {

    //将原型节点表转换为原型基础字段表
    public List<String> ArcheTypeNodeToField();

    //将原型节点表转换为原型基础字段表
    public List<String> ArcheTypeNodeToField(List<ArchetypeNodeClassify> archetypeNodeClassifyList);

    //将拓展节点表转换为拓展基础字段表
    public List<String> ExpandNodeToField();

    //将拓展节点表转换为拓展基础字段表
    public List<String> ExpandNodeToField(List<ExpandNodeClassify> expandNodeClassifyList);
}
