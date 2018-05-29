package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.model.ArchetypeNodeInfo;

import java.util.List;

/**
 * 该类用于操作原型节点分类表
 * @Author CLD
 * @Date 2018/5/22 8:51
 **/
public interface NodeClassifyService{


    /**
     * 该方法将文件形式的节点映射表，存入数据库
     * 用于项目构建阶段
     * @return
     */
  public Boolean FileToDB();

    /**
     * 该方法用于获取原型名列表
     * @return
     */
  public List<String> getArchetypeName();

    /**
     * 该方法用于通过原型名获取相应的节点信息
     * @param archetypeName
     * @return
     */
  public List<ArchetypeNodeInfo> getArchetypeNodeInfoByName(String archetypeName);

    /**
     * 该方法用于保存对原型节点的修改
     * @param archetypeNodeInfoList
     * @return
     */
  public List<String> updataNodeInfo(List<ArchetypeNodeInfo> archetypeNodeInfoList,String archetypeName);

  /**
   * 该方法用于返回原型节点的统计信息
   * @return
   */
  public List<Double> getNodeOverView();
}
