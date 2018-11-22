package com.CLD.dataAnonymization.util.deidentifier;

import java.util.ArrayList;

/**
 * @description:
 * 该类用于实现一些常规的匿名化操作方法
 * 方便外部调用
 * @Author CLD
 * @Date 2018/8/9 12:57
 */
public class EasyAnonymizeAdapter {

    public static ArrayList<ArrayList<String>> ToLimitedSet(ArrayList<ArrayList<String>> data,
                                                            ArrayList<ArrayList<String>> fieldList){
        DataHandle dataHandle=new DataHandle(data);
        dataHandle.setFieldList(fieldList);
        Configuration configuration=new Configuration();
        configuration.setLevel(Configuration.AnonymousLevel.Level1);
        Anonymizer anonymizer=new Anonymizer(dataHandle,configuration);
        anonymizer.anonymize();
        return dataHandle.getData();
    }

    public static ArrayList<ArrayList<String>> ToLimitedSet(ArrayList<ArrayList<String>> data){
        DataHandle dataHandle=new DataHandle(data);
        Configuration configuration=new Configuration();
        configuration.setLevel(Configuration.AnonymousLevel.Level1);
        Anonymizer anonymizer=new Anonymizer(dataHandle,configuration);
        anonymizer.anonymize();
        return dataHandle.getData();
    }

    public static ArrayList<ArrayList<String>> ToSafeHarbor(ArrayList<ArrayList<String>> data,
                                                         ArrayList<ArrayList<String>> fieldList){
        DataHandle dataHandle=new DataHandle(data);
        dataHandle.setFieldList(fieldList);
        Configuration configuration=new Configuration();
        configuration.setLevel(Configuration.AnonymousLevel.Level2);
        Anonymizer anonymizer=new Anonymizer(dataHandle,configuration);
        anonymizer.anonymize();
        return dataHandle.getData();
    }

    public static ArrayList<ArrayList<String>> ToSafeHarbor(ArrayList<ArrayList<String>> data){
        DataHandle dataHandle=new DataHandle(data);
        Configuration configuration=new Configuration();
        configuration.setLevel(Configuration.AnonymousLevel.Level2);
        Anonymizer anonymizer=new Anonymizer(dataHandle,configuration);
        anonymizer.anonymize();
        return dataHandle.getData();
    }
}
