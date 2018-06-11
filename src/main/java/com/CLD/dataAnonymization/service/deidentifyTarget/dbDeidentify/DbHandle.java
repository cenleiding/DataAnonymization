package com.CLD.dataAnonymization.service.deidentifyTarget.dbDeidentify;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * 用于对数据库的操作
 * @Author CLD
 * @Date 2018/5/31 16:39
 **/
public interface DbHandle {

    /**
     * 用于测试数据库是否能够连接成功
     * @param url
     * @param user
     * @param password
     * @return
     */
    public Boolean DbTestConnection(String url, String user, String password);

    /**
     * 连接数据库，返回连接
     * @param url
     * @param user
     * @param password
     * @return
     */
    public Connection getConn(String url, String user, String password);

    /**
     * 用于断开数据库连接
     * @param conn
     */
    public void closeConn(Connection conn);

    /**
     * 遍历当前数据库，获得所有数据表名称
     * @param conn
     * @return
     */
    public ArrayList<String> getDbFromName(Connection conn,String user);

    /**
     * 获取指定名称的数据表字段详细信息
     * @param conn
     * @param fromName
     * @return 字段信息<字段名,字段类型,字段长度,是否为空,主键列表>
     */
    public ArrayList<ArrayList<String>> getDbFromInfo(Connection conn,String user, String fromName);

    /**
     * 根据(新表名、原表信息)建匿名新表
     * @param conn
     * @param newFromName
     * @param fromInfo
     * @return
     */
    public Boolean createMirrorFrom(Connection conn, String newFromName, ArrayList<ArrayList<String>> fromInfo);

    /**
     * 根据(表名，表字段详细信息)获得表单数据
     * @param conn
     * @param fromName
     * @param fromInfo
     * @return
     */
    public ArrayList<ArrayList<String>> getFromData(Connection conn, String fromName, ArrayList<ArrayList<String>> fromInfo);

    /**
     * 将数据插入新表中
     * @param conn
     * @param newFromName
     * @param dataList
     * @return
     */
    public Boolean insertNewFrom(Connection conn, String newFromName, ArrayList<ArrayList<String>> dataList);
}
