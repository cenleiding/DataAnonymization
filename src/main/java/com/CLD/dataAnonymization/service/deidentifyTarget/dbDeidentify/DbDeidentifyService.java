package com.CLD.dataAnonymization.service.deidentifyTarget.dbDeidentify;

import com.CLD.dataAnonymization.model.AnonymizeConfigure;

import java.util.List;

/**
 * @description:该服务用于对数据库进行匿名化操作,并记录处理状态
 * @Author CLD
 * @Date 2018/5/29 8:49
 **/
public interface DbDeidentifyService {

    public void DbDeidentify(String dbType,
                             String host,
                             String port,
                             String databaseName,
                             String user,
                             String password,
                             AnonymizeConfigure anonymizeConfigure);

    public Boolean testConnect(String dbType,
                               String host,
                               String port,
                               String databaseName,
                               String user,
                               String password);

    public List<String> getInfo(String dbType,
                                String host,
                                String port,
                                String databaseName);

    public AnonymizeConfigure getAnonymizeConfigure();
}
