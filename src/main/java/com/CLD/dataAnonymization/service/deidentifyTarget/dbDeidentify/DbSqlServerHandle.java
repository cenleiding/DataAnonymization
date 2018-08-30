package com.CLD.dataAnonymization.service.deidentifyTarget.dbDeidentify;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;

/**
 * @description:该服务实现对SqlServer的操作
 * @Author CLD
 * @Date 2018/5/31 17:01
 **/
@Service("SqlServer")
@Slf4j
public class DbSqlServerHandle implements DbHandle {

    /**
     * 测试连接
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
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
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
            rs = metadata.getTables(null, "dbo", null, new String[]{"TABLE"});// catalog 为数据库名，url中已包括可以不填
            log.info("***代处理数据库列表***");
            while (rs.next()) {
                log.info("数据库名:" + rs.getString(1));
                log.info("表名: " + rs.getString(3));
                log.info("类型: " + rs.getString(4));
                fromNameList.add(rs.getString(3));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
    public ArrayList<ArrayList<String>> getDbFromInfo(Connection conn,String user, String fromName){
        ResultSet rs=null;
        ArrayList<ArrayList<String>> fromInfo=new ArrayList<ArrayList<String>>();
        ArrayList<String> name=new ArrayList<String>();
        ArrayList<String> type=new ArrayList<String>();
        ArrayList<String> length=new ArrayList<String>();
        ArrayList<String> isNull=new ArrayList<String>();
        ArrayList<String> key=new ArrayList<String>();
        ArrayList<String> index=new ArrayList<String>();
        try {
            //表头信息
            rs=conn.getMetaData().getColumns(null,null,fromName,null);
            log.info("表名："+fromName);
            while(rs.next()) {
                log.info(" 列名称: "+ rs.getString("COLUMN_NAME"));
                log.info(" 列类型(DB): " + rs.getString("TYPE_NAME"));
                log.info(" 长度: "+ rs.getInt("COLUMN_SIZE"));
                log.info(" 是否可以为空: "+ rs.getInt("NULLABLE"));
                name.add(rs.getString("COLUMN_NAME"));
                type.add(rs.getString("TYPE_NAME").split(" ")[0].toUpperCase());//去除附加信息如：identity.并转大写
                length.add(String.valueOf(rs.getInt("COLUMN_SIZE")));
                isNull.add(String.valueOf(rs.getInt("NULLABLE")));
            }

            //主键信息
            rs = conn.getMetaData().getPrimaryKeys(null, null,fromName);
            while(rs.next()) {
                log.info("***主键***");
                log.info("COLUMN_NAME: " + rs.getObject(4));
                log.info("KEY_SEQ : " + rs.getObject(5));
                log.info("PK_NAME : " + rs.getObject(6));
                key.add(rs.getObject(4).toString());
            }

            //索引信息
            rs=conn.getMetaData().getIndexInfo(null,null,fromName,false,true);
            while(rs.next()){
                log.info("***索引***");
                log.info("索引名："+rs.getString("INDEX_NAME"));
                log.info("列名："+rs.getString("COLUMN_NAME"));
                log.info("索引类型："+rs.getShort("TYPE"));
                log.info("索引类别："+rs.getString("INDEX_QUALIFIER"));
                log.info("索引是否唯一："+rs.getBoolean("NON_UNIQUE"));
                if(key.contains(rs.getString("COLUMN_NAME"))) continue;
                switch (rs.getShort("TYPE")){
                    case 0: {
                        break;
                    }
                    default:{
                        index.add("CREATE NONCLUSTERED INDEX ["+rs.getString("INDEX_NAME")+"] ON FromName (["+rs.getString("COLUMN_NAME")+"])" );
                        break;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        fromInfo.add(name);
        fromInfo.add(type);
        fromInfo.add(length);
        fromInfo.add(isNull);
        fromInfo.add(key);
        fromInfo.add(index);
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
                if(Integer.valueOf(fromInfo.get(2).get(i))>20000){
                    createSql+="["+fromInfo.get(0).get(i)+"] TEXT,";
                    continue;
                }
                createSql+="["+fromInfo.get(0).get(i)+"] VARCHAR(";
                if(Integer.valueOf(fromInfo.get(2).get(i))<100) createSql+="100)";
                else createSql+=Integer.valueOf(fromInfo.get(2).get(i))+")";
                if(fromInfo.get(3).get(i).equals("0")) createSql+=" not null,";
                else createSql+=",";
            }
            //拼接主键
            if(fromInfo.get(4).size()>0) {
                createSql+="primary key (";
                for(int i=0;i<fromInfo.get(4).size();i++){
                    createSql+="["+fromInfo.get(4).get(i)+"],";
                }
                createSql=createSql.substring(0,createSql.length()-1)+"))";
            }else {
                createSql=createSql.substring(0,createSql.length()-1)+")";
            }

            //建表
            stmt = conn.createStatement();
            stmt.executeUpdate(createSql);
            //加索引
            for(int i=0;i<fromInfo.get(5).size();i++){
                createSql=fromInfo.get(5).get(i).replace("FromName",newFromName);
                stmt.executeUpdate(createSql);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
    public ArrayList<ArrayList<String>> getFromData(Connection conn, String fromName,Integer offset,Integer length, ArrayList<ArrayList<String>> fromInfo){
        Statement stmt=null;
        String key=(fromInfo.get(4).size()>0)?fromInfo.get(4).get(0):fromInfo.get(0).get(0);
        String sql="SELECT * FROM "+fromName+" ORDER BY "+key+" OFFSET "+offset+" ROWS FETCH NEXT "+length+" ROWS ONLY";

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
                    data.add(rs.getObject(name) == null ? "NULL": rs.getObject(name).toString());
                }
                dataList.add(data);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
            insetSql+="["+dataList.get(0).get(i)+"],";
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
            return false;
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

    @Override
    public Integer getFromColumnNum(Connection conn, String fromName) {
        Statement stmt=null;
        Integer columnNum=0;
        String sql = "SELECT COUNT(*) FROM "+fromName;
        try{
            stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(sql);
            while (rs.next()){
                columnNum=rs.getInt(1);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return columnNum;
    }
}
