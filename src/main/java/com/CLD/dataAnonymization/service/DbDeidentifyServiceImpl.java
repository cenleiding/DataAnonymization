package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.dao.h2.entity.DbInfo;
import com.CLD.dataAnonymization.dao.h2.repository.DbInfoRepository;
import com.CLD.dataAnonymization.util.deidentifier.IOAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author CLD
 * @Date 2018/5/31 17:07
 **/
@Service
public class DbDeidentifyServiceImpl implements DbDeidentifyService {

    @Autowired
    @Qualifier("MySql")
    DbHandle dbMySqlHandle;

    @Autowired
    @Qualifier("SqlServer")
    DbHandle dbSqlServerHandle;

    @Autowired
    @Qualifier("Oracle")
    DbHandle dbOracleHandle;

    @Autowired
    DbInfoRepository dbInfoRepository;

    @Autowired
    FieldClassifyService fieldClassifyService;

    @Override
    public void DbDeidentify(String dbType,
                             String host,
                             String port,
                             String databaseName,
                             String user,
                             String password,
                             String method,
                             String fieldFromName) {
        String url="";
        Connection conn=null;
        if(dbType.equals("MySql")){
            url="jdbc:mysql://"+host+":"+port+"/"+databaseName+"?autoReconnect=true&useSSL=false";
            conn=dbMySqlHandle.getConn(url,user,password);
            saveH2(url,"***数据库连接成功***");
            ArrayList<String> fromName=dbMySqlHandle.getDbFromName(conn,user);
            saveH2(url,"***代处理数据库表列表***");
            saveH2(url,fromName.toString());
            for(int i=0;i<fromName.size();i++){
                saveH2(url,"表："+fromName.get(i)+"处理中...");

                ArrayList<ArrayList<String>> fromInfo=dbMySqlHandle.getDbFromInfo(conn,fromName.get(i));
                saveH2(url,"字段总数："+fromInfo.size());

                ArrayList<ArrayList<String>> dataList=dbMySqlHandle.getFromData(conn,fromName.get(i),fromInfo);
                saveH2(url,"表共含记录："+dataList.size()+"条...");

                saveH2(url,"数据处理中...");
                ArrayList<ArrayList<String>> fieldList=fieldClassifyService.getUseFieldByFromName(fieldFromName);
                if(method.equals("SafeHarbor"))
                dataList= IOAdapter.ToSafeHarbor(dataList,fieldList);
                if(method.equals("LimitedSet"))
                dataList= IOAdapter.ToLimitedSet(dataList,fieldList);
                saveH2(url,"数据处理完毕！");

                dbMySqlHandle.createMirrorFrom(conn,fromName.get(i)+"_"+method,fromInfo);
                saveH2(url,"匿名表："+fromName.get(i)+"_"+method+"创建成功！");

                dbMySqlHandle.insertNewFrom(conn,fromName.get(i)+"_"+method,dataList);
                saveH2(url,"匿名数据插入成功！");
                saveH2(url,"****************");
            }
            dbMySqlHandle.closeConn(conn);
        }
        if(dbType.equals("SqlServer")){
            url="jdbc:sqlserver://"+host+":"+port+";DatabaseName="+databaseName;
        }
        if(dbType.equals("Oracle")){
            url="jdbc:oracle:thin:@"+host+":"+port+":"+databaseName;
        }

        deletH2(url);
    }

    /**
     * 测试数据库连接
     * @param dbType
     * @param host
     * @param port
     * @param databaseName
     * @param user
     * @param password
     * @return
     */
    @Override
    public Boolean testConnect(String dbType, String host, String port, String databaseName, String user, String password) {
        String url="";
        Boolean test=false;
        if(dbType.equals("MySql")) {
            url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?autoReconnect=true&useSSL=false";
            test=dbMySqlHandle.DbTestConnection(url,user,password);
        }
        if(dbType.equals("SqlServer")){
            url="jdbc:sqlserver://"+host+":"+port+";DatabaseName="+databaseName;
            test=dbSqlServerHandle.DbTestConnection(url,user,password);
        }
        if(dbType.equals("Oracle")){
            url="jdbc:oracle:thin:@"+host+":"+port+":"+databaseName;
            test=dbOracleHandle.DbTestConnection(url,user,password);
        }
        return test;
    }

    /**
     * 用于获取处理信息
     * @param dbType
     * @param host
     * @param port
     * @param databaseName
     * @return
     */
    @Override
    public List<String> getInfo(String dbType, String host, String port, String databaseName) {
        String url="";
        if(dbType.equals("MySql")) {
            url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?autoReconnect=true&useSSL=false";
        }
        if(dbType.equals("SqlServer")){
            url="jdbc:sqlserver://"+host+":"+port+";DatabaseName="+databaseName;
        }
        if(dbType.equals("Oracle")){
            url="jdbc:oracle:thin:@"+host+":"+port+":"+databaseName;
        }
        List<DbInfo> dbInfoList=dbInfoRepository.findAllByUrl(url);
        List<String> out=new ArrayList<String>();
        for(DbInfo dbInfo:dbInfoList){
            out.add(dbInfo.getState());
        }
        return out;
    }

    private void saveH2 (String url,String s){
        DbInfo dbInfo=new DbInfo();
        dbInfo.setUrl(url);
        dbInfo.setState(s);
        dbInfoRepository.save(dbInfo);
    }

    private void deletH2(String url){
        dbInfoRepository.deleteAllByUrl(url);
    }
}
