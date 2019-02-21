package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import com.CLD.dataAnonymization.util.deidentifier.DataHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @description: 该类用于自动生成泛化层次结构
 * @Author CLD
 * @Date 2018/8/7 10:52
 */
public class HierarchyBuilder {

    /**
     * 检查相应列的泛化层次结构是否存在。
     * @param dataHandle
     * @return
     */
    public static Boolean checkHierarchy(DataHandle dataHandle){
        ArrayList<ArrayList<String>> data  =  dataHandle.getData();
        HashSet<String> QI_DateRecord  =  dataHandle.getQI_DateRecord();
        HashSet<String> QI_String      =  dataHandle.getQI_String();
        HashSet<String> SI_String      =  dataHandle.getSI_String();
        HashMap<String,ArrayList<ArrayList<String>>> hierarchy=dataHandle.getHierarchy();
        for(int i=0;i<data.size();i++) {
            String h=data.get(i).get(0).toLowerCase()
                    .replace(".","")
                    .replace("_","")
                    .replace("-","")
                    .replace("*","");
            if ((hierarchy==null || !hierarchy.keySet().contains(data.get(i).get(0))) &&
                    (QI_DateRecord.contains(h)||QI_String.contains(h)||SI_String.contains(h))){
                if(QI_DateRecord.contains(h))dateHierarchyBuilder(dataHandle,i);
                else stringHierarchyBuilder(dataHandle,i);
            }
        }
        return true;
    }

    /**
     * 分为6级
     * yyyy-MM-dd
     * yyyy-MM
     * yyyy
     * [yyyy,yyyy+5]5年为期限 [0-4][5-9]
     * [yyy0-yyy9]
     * "***"
     * @param dataHandle
     * @param col
     * @return
     */
    private static Boolean dateHierarchyBuilder(DataHandle dataHandle,Integer col){
        ArrayList<ArrayList<String>> hierarchy=new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> data     =dataHandle.getData();
        for(int i=1;i<data.get(col).size();i++){
            ArrayList<String> h=new ArrayList<String>();
            String d= data.get(col).get(i);
            if(d.equals("")){
                for(int j=0;j<6;j++)
                h.add("***");
            }else {
                h.add(d);
                h.add(d.split("-")[0]+"-"+d.split("-")[1]);
                h.add(d.split("-")[0]);
                if(Integer.valueOf(d.split("-")[0].charAt(3))<5)
                    h.add("["+d.split("-")[0].substring(0,3)+"0-"+d.split("-")[0].substring(0,3)+"4]");
                else
                    h.add("["+d.split("-")[0].substring(0,3)+"5-"+d.split("-")[0].substring(0,3)+"9]");
                h.add("["+d.split("-")[0].substring(0,3)+"0-"+d.split("-")[0].substring(0,3)+"9]");
                h.add("***");
            }
            hierarchy.add(h);
        }
        dataHandle.addHierarchy(data.get(col).get(0),hierarchy);
        return true;
    }

    /**
     * 分为2级
     * 一：原数据
     * 二： ***
     * @param dataHandle
     * @param col
     * @return
     */
    private static Boolean stringHierarchyBuilder(DataHandle dataHandle,Integer col){
        ArrayList<ArrayList<String>> hierarchy=new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> data     =dataHandle.getData();
        for(int i=1;i<data.get(col).size();i++){
            ArrayList<String> h=new ArrayList<String>();
            h.add(data.get(col).get(i));
            h.add("***");
            hierarchy.add(h);
        }
        dataHandle.addHierarchy(data.get(col).get(0),hierarchy);
        return true;
    }
}
