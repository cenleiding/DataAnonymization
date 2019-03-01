package com.CLD.dataAnonymization.service.deidentifyTarget.dbDeidentify;

import com.CLD.dataAnonymization.dao.h2.entity.DbInfo;
import com.CLD.dataAnonymization.dao.h2.entity.FieldClassifyUsageCount;
import com.CLD.dataAnonymization.dao.h2.repository.DbInfoRepository;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyUsageCountRepository;
import com.CLD.dataAnonymization.model.AnonymizeConfigure;
import com.CLD.dataAnonymization.service.deidentifyTarget.EasyUtil;
import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
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

    @Autowired
    FieldClassifyUsageCountRepository fieldClassifyUsageCountRepository;

    @Autowired
    EasyUtil easyUtil;

    @Override
    public void DbDeidentify(String dbType,
                             String host,
                             String port,
                             String databaseName,
                             String user,
                             String password,
                             AnonymizeConfigure anonymizeConfigure) {
        String url="";
        Integer length=10000;
        Connection conn=null;

        //开始匿名
        if(dbType.equals("MySql")){
            url="jdbc:mysql://"+host+":"+port+"/"+databaseName+"?autoReconnect=true&useSSL=false";
            conn=dbMySqlHandle.getConn(url,user,password);
            saveH2(url,"***数据库连接成功***");
            ArrayList<String> fromName=dbMySqlHandle.getDbFromName(conn,user);
            saveH2(url,"***代处理数据库表列表***");
            saveH2(url,fromName.toString());
            for(int i=0;i<fromName.size();i++){

                Boolean success=false;
                saveH2(url,"****************");
                saveH2(url,"表："+fromName.get(i)+"处理中...");

                ArrayList<ArrayList<String>> fromInfo=dbMySqlHandle.getDbFromInfo(conn,user,fromName.get(i));
                if(fromInfo==null){saveH2(url,"!!!字段获取失败!!!");continue;}
                saveH2(url,"字段总数："+fromInfo.size());

                success=dbMySqlHandle.createMirrorFrom(conn,fromName.get(i)+"_"+anonymizeConfigure.getLevel(),fromInfo);
                if(success==false) { saveH2(url,"!!!匿名表："+fromName.get(i)+"_"+anonymizeConfigure.getLevel()+"创建失败!!!");continue;}
                saveH2(url,"匿名表："+fromName.get(i)+"_"+anonymizeConfigure.getLevel()+"创建成功！");

                Integer columnNum=dbMySqlHandle.getFromColumnNum(conn,fromName.get(i));
                if(columnNum==null) { saveH2(url,"!!!表单行数获取失败!!!");continue;}
                saveH2(url,"表共含记录："+columnNum+"条...");
                saveH2(url,"数据开始处理......");
                Integer offset=0;
                while(offset<columnNum){
                    ArrayList<ArrayList<String>> dataList=dbMySqlHandle.getFromData(conn,fromName.get(i),offset,length,fromInfo);
                    if(dataList==null){saveH2(url,"!!!表获取数据失败!!!");break;}
                    if(offset+length<columnNum)saveH2(url,"获取第"+(offset+1)+"——"+(offset+length)+"条数据");
                    else saveH2(url,"获取第"+(offset+1)+"——"+columnNum+"条数据");
                    //匿名化
                    easyUtil.deidentify_run(dataList,anonymizeConfigure);

                    if(dataList==null) {saveH2(url,"!!!处理失败!!!");break;}
                    saveH2(url,"处理完毕！");
                    success=dbMySqlHandle.insertNewFrom(conn,fromName.get(i)+"_"+anonymizeConfigure.getLevel(),dataList);
                    if(success==false) { saveH2(url,"!!!插入失败!!!");break;}
                    saveH2(url,"插入成功！");
                    offset+=length;
                }

                saveH2(url,"****************");
            }
            dbMySqlHandle.closeConn(conn);
        }
        if(dbType.equals("SqlServer")){
            url="jdbc:sqlserver://"+host+":"+port+";DatabaseName="+databaseName;
            conn=dbSqlServerHandle.getConn(url,user,password);
            saveH2(url,"***数据库连接成功***");
            ArrayList<String> fromName=dbSqlServerHandle.getDbFromName(conn,user);
            saveH2(url,"***代处理数据库表列表***");
            saveH2(url,fromName.toString());
            for(int i=0;i<fromName.size();i++){
                Boolean success=false;
                saveH2(url,"****************");
                saveH2(url,"表："+fromName.get(i)+"处理中...");

                ArrayList<ArrayList<String>> fromInfo=dbSqlServerHandle.getDbFromInfo(conn,user,fromName.get(i));
                if(fromInfo==null){saveH2(url,"!!!字段获取失败!!!");continue;}
                saveH2(url,"字段总数："+fromInfo.size());

                success=dbSqlServerHandle.createMirrorFrom(conn,fromName.get(i)+"_"+anonymizeConfigure.getLevel(),fromInfo);
                if(success==false) { saveH2(url,"!!!匿名表："+fromName.get(i)+"_"+anonymizeConfigure.getLevel()+"创建失败!!!");continue;}
                saveH2(url,"匿名表："+fromName.get(i)+"_"+anonymizeConfigure.getLevel()+"创建成功！");

                Integer columnNum=dbMySqlHandle.getFromColumnNum(conn,fromName.get(i));
                if(columnNum==null) { saveH2(url,"!!!表单行数获取失败!!!");continue;}
                saveH2(url,"表共含记录："+columnNum+"条...");
                saveH2(url,"数据开始处理......");
                Integer offset=0;
                while(offset<columnNum){
                    ArrayList<ArrayList<String>> dataList=dbSqlServerHandle.getFromData(conn,fromName.get(i),offset,length,fromInfo);
                    if(dataList==null){saveH2(url,"!!!表获取数据失败!!!");break;}
                    if(offset+length<columnNum)saveH2(url,"获取第"+(offset+1)+"——"+(offset+length)+"条数据");
                    else saveH2(url,"获取第"+(offset+1)+"——"+columnNum+"条数据");
                    //匿名化
                    easyUtil.deidentify_run(dataList,anonymizeConfigure);

                    if(dataList==null) {saveH2(url,"!!!处理失败!!!");break;}
                    saveH2(url,"处理完毕！");

                    success=dbSqlServerHandle.insertNewFrom(conn,fromName.get(i)+"_"+anonymizeConfigure.getLevel(),dataList);
                    if(success==false) { saveH2(url,"!!!数据插入失败!!!");break;}
                    saveH2(url,"插入成功！");
                    offset+=length;
                }

                saveH2(url,"****************");
            }
            dbSqlServerHandle.closeConn(conn);
        }
        if(dbType.equals("Oracle")){
            url="jdbc:oracle:thin:@"+host+":"+port+":"+databaseName;
            conn=dbOracleHandle.getConn(url,user,password);
            saveH2(url,"***数据库连接成功***");
            ArrayList<String> fromName=dbOracleHandle.getDbFromName(conn,user);
            saveH2(url,"***代处理数据库表列表***");
            saveH2(url,fromName.toString());
            for(int i=0;i<fromName.size();i++){
                Boolean success=false;
                saveH2(url,"****************");
                saveH2(url,"表："+fromName.get(i)+"处理中...");

                ArrayList<ArrayList<String>> fromInfo=dbOracleHandle.getDbFromInfo(conn,user,fromName.get(i));
                if(fromInfo==null){saveH2(url,"!!!字段获取失败!!!");continue;}
                saveH2(url,"字段总数："+fromInfo.size());

                success=dbOracleHandle.createMirrorFrom(conn,fromName.get(i)+"_"+anonymizeConfigure.getLevel(),fromInfo);
                if(success==false) { saveH2(url,"!!!匿名表："+fromName.get(i)+"_"+anonymizeConfigure.getLevel()+"创建失败!!!");continue;}
                saveH2(url,"匿名表："+fromName.get(i)+"_"+anonymizeConfigure.getLevel()+"创建成功！");

                Integer columnNum=dbMySqlHandle.getFromColumnNum(conn,fromName.get(i));
                if(columnNum==null) { saveH2(url,"!!!表单行数获取失败!!!");continue;}
                saveH2(url,"表共含记录："+columnNum+"条...");
                saveH2(url,"数据开始处理......");
                Integer offset=0;
                while(offset<columnNum){
                    ArrayList<ArrayList<String>> dataList=dbOracleHandle.getFromData(conn,fromName.get(i),offset,length,fromInfo);
                    if(dataList==null){saveH2(url,"!!!表获取数据失败!!!");break;}
                    if(offset+length<columnNum)saveH2(url,"获取第"+(offset+1)+"——"+(offset+length)+"条数据");
                    else saveH2(url,"获取第"+(offset+1)+"——"+columnNum+"条数据");

                    //匿名化
                    easyUtil.deidentify_run(dataList,anonymizeConfigure);

                    if(dataList==null) {saveH2(url,"!!!处理失败!!!");break;}
                    saveH2(url,"处理完毕！");

                    success=dbOracleHandle.insertNewFrom(conn,fromName.get(i)+"_"+anonymizeConfigure.getLevel(),dataList);
                    if(success==false) { saveH2(url,"!!!插入失败!!!");break;}
                    saveH2(url,"插入成功！");
                    offset+=length;
                }
                saveH2(url,"****************");
            }
            dbOracleHandle.closeConn(conn);
        }

        //表单使用记录
        FieldClassifyUsageCount fieldClassifyUsageCount=fieldClassifyUsageCountRepository.findByFormName(anonymizeConfigure.getFieldFormName());
        fieldClassifyUsageCount.setCount(fieldClassifyUsageCount.getCount()+1);
        fieldClassifyUsageCountRepository.save(fieldClassifyUsageCount);

        try {
            Thread.currentThread().sleep(2000);
            deletH2(url);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        System.out.println(s);
    }


    private void deletH2(String url){
        dbInfoRepository.deleteAllByUrl(url);
    }
}
