package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.model.ExpandNodeInfo;

import java.util.List;

/**
 * 该方法用于操作拓展节点分类表
 * @Author CLD
 * @Date 2018/6/4 9:09
 **/
public interface ExpandNodeClassifyService {

    /**
     * 该方法将文件形式的节点映射表，存入数据库
     * 用于项目构建阶段
     * @return
     */
    public Boolean FileToDB();

    /**
     * 该方法将数据库形式的映射表保存为文件形式
     * 用于保存内容修改
     * @return
     */
    public Boolean DBToFile();

    /**
     * 该方法用于获取拓展文件列表
     * @return
     */
    public List<String> getFileName();

    /**
     * 该方法用于根据文件名获取相应表名列表
     * @param FileName
     * @return
     */
    public List<String> getFromNameByFileName(String FileName);

    /**
     * 该方法用于通过文件名，表名获得相应的节点信息
     * @param fileName
     * @param fromName
     * @return
     */
    public List<ExpandNodeInfo> getNodeInfoByName(String fileName, String fromName);

    /**
     * 该方法用于保存对原型节点的修改
     * @param expandNodeInfoList
     * @param fileName
     * @param fromName
     * @return
     */
    public List<String> updataNodeInfo(List<ExpandNodeInfo> expandNodeInfoList,String fileName,String fromName);
}
