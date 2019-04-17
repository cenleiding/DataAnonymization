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

    public static void main(String[] args){
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        ArrayList<String> d1 = new ArrayList<>();
        d1.add("名族");
        d1.add("汉");
        d1.add("汉");
        d1.add("汉");
        d1.add("汉");
        d1.add("维吾尔族");
        d1.add("维吾尔族");
        d1.add("维吾尔族");
        d1.add("土家族");

        ArrayList<String> d2 = new ArrayList<>();
        d2.add("入院时间");
        d2.add("2018-03-05");
        d2.add("2018-03-06");
        d2.add("2019-04-05");
        d2.add("2019-05-05");
        d2.add("2000-10-10");
        d2.add("2010-05-15");
        d2.add("2013-12-11");
        d2.add("2014-12-21");

        data.add(d1);
        data.add(d2);
    }

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
