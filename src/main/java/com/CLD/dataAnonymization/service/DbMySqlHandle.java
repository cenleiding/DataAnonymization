package com.CLD.dataAnonymization.service;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;

/**
 * 该服务实现对MySQL的操作
 * @Author CLD
 * @Date 2018/5/31 16:40
 **/
@Service("MySql")
public class DbMySqlHandle implements DbHandle{

    public void DbDeidentify(String url,String user,String password,String fieldFromName,String identityType){
        Connection conn=null;
        try {
            conn=getConn(url,user,password);
            ArrayList<String> fromName=getDbFromName(conn,user);
            for(int i=0;i<fromName.size();i++){
                ArrayList<ArrayList<String>> fromInfo=getDbFromInfo(conn,fromName.get(i));
                createMirrorFrom(conn,fromName.get(i)+"_"+identityType,fromInfo);
                System.out.println("***表单："+fromName.get(i)+"_"+identityType+" 创建成功！***");
                ArrayList<ArrayList<String>> dataList=getFromData(conn,fromName.get(i),fromInfo);
                insertNewFrom(conn,fromName.get(i)+"_"+identityType,dataList);
                System.out.println("***表单："+fromName.get(i)+"_"+identityType+" 数据插入成功！***");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用于测试数据库是否能够连接成功
     * @param url
     * @param user
     * @param password
     * @return
     */
    @Override
    public Boolean DbTestConnection(String url, String user, String password) {
        Connection conn=getConn(url,user,password);
        if(conn==null) return false;
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 连接数据库，返回连接
     * @param url
     * @param user
     * @param password
     * @return
     */
    @Override
    public Connection getConn(String url, String user, String password){
        Connection conn=null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url,user,password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 用于断开连接
     * @param conn
     */
    @Override
    public void closeConn(Connection conn) {
        if(conn==null) return;
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历当前数据库，获得所有数据表名称
     * @param conn
     * @return
     */
    @Override
    public ArrayList<String> getDbFromName(Connection conn,String user){
        DatabaseMetaData metadata=null;
        ResultSet rs=null;
        ArrayList<String> fromNameList=new ArrayList<String>();
        try {
            metadata = conn.getMetaData();
            rs = metadata.getTables(null, null, null, new String[]{"TABLE"});// catalog 为数据库名，url中已包括可以不填
            System.out.println("***代处理数据库表列表***");
            while (rs.next()) {
                System.out.println("数据库名:" + rs.getString(1));
                System.out.println("表名: " + rs.getString(3));
                System.out.println("类型: " + rs.getString(4));
                System.out.println("");
                fromNameList.add(rs.getString(3));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fromNameList;
    }

    /**
     * 获取指定名称的数据表字段详细信息
     * @param conn
     * @param fromName
     * @return 字段信息<字段名,字段类型,字段长度,是否为空,主键列表>
     */
    @Override
    public ArrayList<ArrayList<String>> getDbFromInfo(Connection conn, String fromName){
        ResultSet rs=null;
        ArrayList<ArrayList<String>> fromInfo=new ArrayList<ArrayList<String>>();
        ArrayList<String> name=new ArrayList<String>();
        ArrayList<String> type=new ArrayList<String>();
        ArrayList<String> length=new ArrayList<String>();
        ArrayList<String> isNull=new ArrayList<String>();
        ArrayList<String> key=new ArrayList<String>();
        try {
            //表头信息
            rs=conn.getMetaData().getColumns(null,null,fromName,null);
            System.out.println("表名："+fromName);
            while(rs.next()) {
                System.out.print(" 列名称: "+ rs.getString("COLUMN_NAME"));
                System.out.print(" 列类型(DB): " + rs.getString("TYPE_NAME"));
                System.out.print(" 长度: "+ rs.getInt("COLUMN_SIZE"));
                System.out.print(" 是否可以为空: "+ rs.getInt("NULLABLE"));
                System.out.println();
                name.add(rs.getString("COLUMN_NAME"));
                type.add(rs.getString("TYPE_NAME").split(" ")[0].toUpperCase());//去除附加信息如：identity
                length.add(String.valueOf(rs.getInt("COLUMN_SIZE")));
                isNull.add(String.valueOf(rs.getInt("NULLABLE")));
            }

            //主键信息
            rs = conn.getMetaData().getPrimaryKeys(null, null,fromName);
            while(rs.next()) {
                System.out.println();
                System.out.println("***主键***");
                System.out.println("COLUMN_NAME: " + rs.getObject(4));
                System.out.println("KEY_SEQ : " + rs.getObject(5));
                System.out.println("PK_NAME : " + rs.getObject(6));
                key.add(rs.getObject(4).toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        fromInfo.add(name);
        fromInfo.add(type);
        fromInfo.add(length);
        fromInfo.add(isNull);
        fromInfo.add(key);
        return fromInfo;
    }

    /**
     * 根据(新表名、原表信息)建匿名新表
     * @param conn
     * @param newFromName
     * @param fromInfo
     * @return
     */
    @Override
    public Boolean createMirrorFrom(Connection conn, String newFromName, ArrayList<ArrayList<String>> fromInfo){
        Statement stmt=null;
        ResultSet rs=null;
        DatabaseMetaData metadata=null;
        String createSql="CREATE TABLE "+newFromName+"(";
        try {
            //拼接表头基本信息
            for (int i =0; i <fromInfo.get(0).size(); i++) {
                createSql+=fromInfo.get(0).get(i)+" VARCHAR(";
                if(Integer.valueOf(fromInfo.get(2).get(i))<32) createSql+="32)";
                else createSql+=Integer.valueOf(fromInfo.get(2).get(i))+")";
                if(fromInfo.get(3).get(i).equals("0")) createSql+=" not null,";
                else createSql+=",";
            }
            //拼接主键
            for(int i=0;i<fromInfo.get(4).size();i++){
                createSql+="primary key ("+fromInfo.get(4).get(i)+",";
            }
            createSql=createSql.substring(0,createSql.length()-1)+"))";
            //建表
            stmt = conn.createStatement();
            stmt.executeUpdate(createSql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 根据(表名，表字段详细信息)获得表单数据
     * @param conn
     * @param fromName
     * @param fromInfo
     * @return
     */
    @Override
    public ArrayList<ArrayList<String>> getFromData(Connection conn, String fromName, ArrayList<ArrayList<String>> fromInfo){
        Statement stmt=null;
        String sql = "SELECT * FROM "+fromName;
        ArrayList<ArrayList<String>> dataList=new ArrayList<ArrayList<String>>();
        dataList.add(fromInfo.get(0));
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                ArrayList<String> data=new ArrayList<String>();
                for(int i=0;i<fromInfo.get(0).size();i++){
                    String name=fromInfo.get(0).get(i);
                    String type=fromInfo.get(1).get(i);
                    if(type.equals("VARCHAR"))  data.add(rs.getString(name) == null ? "NULL": rs.getString(name));
                    if(type.equals("DATETIME")) data.add(rs.getDate(name)==null ? "NULL" : rs.getDate(name).toString());
                    if(type.equals("BIT"))      data.add(String.valueOf(rs.getByte(name)));
                    if(type.equals("INT"))      data.add(String.valueOf(rs.getInt(name)));
                }
                dataList.add(data);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    /**
     * 将数据插入新表中
     * @param conn
     * @param newFromName
     * @param dataList
     * @return
     */
    @Override
    public Boolean insertNewFrom(Connection conn, String newFromName, ArrayList<ArrayList<String>> dataList){
        PreparedStatement pstmt=null;
        String insetSql="insert into "+newFromName+"(";
        for(int i=0;i<dataList.get(0).size();i++)
            insetSql+=dataList.get(0).get(i)+",";
        insetSql=insetSql.substring(0,insetSql.length()-1)+") VALUES (";
        for(int i=0;i<dataList.get(0).size();i++)
            insetSql+="?,";
        insetSql=insetSql.substring(0,insetSql.length()-1)+")";
        try {
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(insetSql);
            for(int i=1;i<dataList.size();i++){
                for(int j=0;j<dataList.get(0).size();j++){
                    pstmt.setString(j+1,dataList.get(i).get(j));
                }
                pstmt.addBatch();
                if(i%1000==0){
                    pstmt.executeBatch();
                    conn.commit();
                    pstmt.clearBatch();
                }
            }
            pstmt.executeBatch();
            conn.commit();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                conn.setAutoCommit(true);
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
