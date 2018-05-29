package com.CLD.dataAnonymization.util.deidentifier;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * 此类用于外部调用匿名方法
 * 并进行参数设置
 * @Author CLD
 * @Date 2018/4/12 10:14
 **/
public class IOAdapter {

    /**用于外部调用安全屋方法
     * @param data
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<ArrayList<String>> ToSafeHarbor(ArrayList<ArrayList<String>> data,
                                                            ArrayList<ArrayList<String>> fieldList) {
        ArrayList<String> EI=new ArrayList<String>();
        ArrayList<String> QI_Geographic=new ArrayList<String>();;
        ArrayList<String> QI_Date=new ArrayList<String>();
        ArrayList<String> QI_Number=new ArrayList<String>();
        ArrayList<String> QI_String=new ArrayList<String>();
        ArrayList<String> UI=new ArrayList<String>();
        ArrayList<String> Geographic=new ArrayList<String>();
        for(int i=0;i<fieldList.size();i++){
            if(fieldList.get(i).get(1).equals("EI"))            EI.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Geographic")) QI_Geographic.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Date"))       QI_Date.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Number"))     QI_Number.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_String"))     QI_String.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("UI"))            UI.add(fieldList.get(i).get(0));
        }
        Geographic=FieldHandle.readAddress().get(0);
        data=Hippa.Identity("SafeHarbor",data,EI,QI_Geographic,QI_Date,QI_Number,QI_String,UI,Geographic);
        return data;
    }


    /**
     * 用于外部调用有限数据集方法
     * @param data
     * @param data
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<ArrayList<String>> ToLimitedSet(ArrayList<ArrayList<String>> data,
                                                            ArrayList<ArrayList<String>> fieldList) {
        ArrayList<String> EI=new ArrayList<String>();
        ArrayList<String> QI_Geographic=new ArrayList<String>();;
        ArrayList<String> QI_Date=new ArrayList<String>();
        ArrayList<String> QI_Number=new ArrayList<String>();
        ArrayList<String> QI_String=new ArrayList<String>();
        ArrayList<String> UI=new ArrayList<String>();
        ArrayList<String> Geographic=new ArrayList<String>();
        for(int i=0;i<fieldList.size();i++){
            if(fieldList.get(i).get(1).equals("EI"))            EI.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Geographic")) QI_Geographic.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Date"))       QI_Date.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Number"))     QI_Number.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_String"))     QI_String.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("UI"))            UI.add(fieldList.get(i).get(0));
        }
        Geographic.addAll(FieldHandle.readAddress().get(0));
        Geographic.addAll(FieldHandle.readAddress().get(1));
        data=Hippa.Identity("LimitedSet",data,EI,QI_Geographic,QI_Date,QI_Number,QI_String,UI,Geographic);
        return data;
    }

}
