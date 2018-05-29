package com.CLD.dataAnonymization.service;

import java.sql.*;

/**
 * @Author CLD
 * @Date 2018/5/29 9:02
 **/
public class DbDeidentifyServiceImpl implements DbDeidentifyService{

    public static void MySqlDeidentify(String url,String user,String password){
        Connection conn=null;
        Statement stmt=null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url,user,password);

            //获取数据库中表信息
            DatabaseMetaData metadata = conn.getMetaData();

            //获取表内容
            // catalog 为数据库名，url中已包括可以不填
            ResultSet rs1 = metadata.getTables(null, null, null, null);
            while (rs1.next()) {
                System.out.println();
                System.out.println("数据库名:"+ rs1.getString(1));
                System.out.println("表名: "+rs1.getString(3));
                System.out.println("类型: "+rs1.getString(4));

                stmt = conn.createStatement();
                String sql = "SELECT * FROM "+rs1.getString(3);
                ResultSet rs2 = stmt.executeQuery(sql);
                //获取每张表的字段名
                String createSql="CREATE TABLE "+rs1.getString(3)+"_new(";
                String key="";
                ResultSetMetaData rsme = rs2.getMetaData();
                System.out.println("列数："+ rsme.getColumnCount());
                for (int i = 1; i <=rsme.getColumnCount() ; i++) {
                    System.out.println();
                    System.out.print(" 列名称: "+ rsme.getColumnName(i));
                    System.out.print(" 列类型(DB): " + rsme.getColumnTypeName(i));
                    System.out.print(" 长度: "+ rsme.getPrecision(i) );
                    System.out.print(" 是否自动编号: "+ rsme.isAutoIncrement(i));
                    System.out.print(" 是否可以为空: "+ rsme.isNullable(i));
                    System.out.print(" 是否可以写入: "+ rsme.isReadOnly(i));
                    createSql+=rsme.getColumnName(i)+" VARCHAR(";
                    if(rsme.getPrecision(i)<32) createSql+="32)";
                    else createSql+=rsme.getPrecision(i)+")";
                    if(rsme.isNullable(i)==0) createSql+=" not null,";
                    else createSql+=",";
                }
                rs2.close();
                //获取主键
                ResultSet rs3 = metadata.getPrimaryKeys(null, null, rs1.getString(3));
                while(rs3.next()) {
                    createSql+="primary key ("+rs3.getObject(4)+",";
                    System.out.println("***主键***");
                    System.out.println("COLUMN_NAME: " + rs3.getObject(4));
                    System.out.println("KEY_SEQ : " + rs3.getObject(5));
                    System.out.println("PK_NAME : " + rs3.getObject(6));
                }
                createSql=createSql.substring(0,createSql.length()-1)+"))";
                rs3.close();
                //新建副本表
                stmt.executeUpdate(createSql);
            }
            rs1.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static void main (String[] args){
        MySqlDeidentify("jdbc:mysql://localhost:3306/test","root","123456");
    }
}
