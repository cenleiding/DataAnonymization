package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.exceptions.RollbackRequiredException;
import org.deidentifier.arx.metric.Metric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.min;

/**
 * @description:该类提供T-closeness方法，当不存在敏感信息时退化为K-anonymous.
 * 方法实现参考ARX工程
 * @Author CLD
 * @Date 2018/8/6 9:03
 */
public class Tcloseness {

    public static Boolean tClosenessHandle(ArrayList<ArrayList<String>> data,
                                           ArrayList<Integer> kcol,
                                           ArrayList<Integer> tcol,
                                           Integer K,
                                           Double T,
                                           Double SuppressionLimit,
                                           HashMap<String,ArrayList<ArrayList<String>>> hierarchy){
        if(kcol.size()==0) return true;
        K= min(K,data.get(0).size()-1);
        try {
        Data arxData=Data.create(dataInstall(data,kcol,tcol));
        HashMap<String,AttributeType.Hierarchy> hierarchyHashMap= getHierarchyMap(hierarchy);
        setAttributeType(data,arxData,kcol,tcol,hierarchyHashMap);
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXConfiguration config = ARXConfiguration.create();
        setConfig(data,tcol,hierarchyHashMap,config,K,T,SuppressionLimit);
        ARXResult result = anonymizer.anonymize(arxData, config);
        DataHandle optimum = result.getOutput();
        localRecoding(result,optimum,SuppressionLimit);
        updataData(data,optimum,kcol,tcol);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("T-closeness 失败！");
            return false;
        }
        return true;
    }

    /**
     * 局部重编码
     * @param result
     * @param optimum
     */
    private static void localRecoding(ARXResult result, DataHandle optimum,Double SuppressionLimit) {
        try {
            result.optimizeIterativeFast(optimum, 1-SuppressionLimit);
        } catch (RollbackRequiredException e) {
            optimum = result.getOutput();
        }
    }

    /**
     * 数据格式转换并导入ARX
     * @param data
     * @param kcol
     * @param tcol
     * @return
     */
    private static List<String[]> dataInstall(ArrayList<ArrayList<String>> data,
                                              ArrayList<Integer> kcol,
                                              ArrayList<Integer> tcol){
        List<String[]> outData=new ArrayList<String[]>();
        ArrayList<Integer> col= (ArrayList<Integer>) kcol.clone();
        col.addAll(tcol);
        for(int i=0;i<data.get(0).size();i++){
            String[] s=new String[col.size()];
            for(int c=0;c<col.size();c++){
                s[c]=data.get(col.get(c)).get(i);
            }
            outData.add(s);
        }
        return outData;
    }

    /**
     * 以Hash表形式输出数据的泛化层次结构
     * @param hierarchy
     * @return
     */
    private static HashMap<String,AttributeType.Hierarchy> getHierarchyMap(HashMap<String,ArrayList<ArrayList<String>>> hierarchy){
        HashMap<String,AttributeType.Hierarchy> outHierarchy=new HashMap<String,AttributeType.Hierarchy>();
        for(String attribute : hierarchy.keySet()){
            List<String[]> hie=new ArrayList<String[]>();
            for(int i=0;i<hierarchy.get(attribute).size();i++){
                String[] S= new String[hierarchy.get(attribute).get(i).size()];
                for(int j=0;j<hierarchy.get(attribute).get(i).size();j++)
                    S[j]=hierarchy.get(attribute).get(i).get(j);
                hie.add(S);
            }
            AttributeType.Hierarchy H= AttributeType.Hierarchy.create(hie);
            outHierarchy.put(attribute,H);
        }
        return outHierarchy;
    }

    /**
     * 该方法进行数据类型的设定
     * @param data
     * @param arxData
     * @param kcol
     * @param tcol
     * @param hierarchyHashMap
     * @return
     */
    private static Boolean setAttributeType(ArrayList<ArrayList<String>> data,
                                            Data arxData,
                                            ArrayList<Integer> kcol,
                                            ArrayList<Integer> tcol,
                                            HashMap<String,AttributeType.Hierarchy> hierarchyHashMap){
        for(int column:kcol){
            arxData.getDefinition().setAttributeType(data.get(column).get(0),hierarchyHashMap.get(data.get(column).get(0)));
        }
        for(int column:tcol){
            arxData.getDefinition().setAttributeType(data.get(column).get(0),AttributeType.SENSITIVE_ATTRIBUTE);
        }
        return true;
    }

    /**
     * 该方法用于对config进行设置
     * @param data
     * @param tcol
     * @param hierarchyHashMap
     * @param config
     * @param K
     * @param T
     * @param SuppressionLimit
     * @return
     */
     private static Boolean setConfig(ArrayList<ArrayList<String>> data,
                                            ArrayList<Integer> tcol,
                                            HashMap<String,AttributeType.Hierarchy> hierarchyHashMap,
                                            ARXConfiguration config,
                                            Integer K,
                                            Double T,
                                            Double SuppressionLimit){
         config.addPrivacyModel(new KAnonymity(K));
         for(int tcolumn:tcol){
             config.addPrivacyModel(new HierarchicalDistanceTCloseness(data.get(tcolumn).get(0), T, hierarchyHashMap.get(data.get(tcolumn).get(0))));
         }
         config.setSuppressionLimit(SuppressionLimit);
         config.setQualityModel(Metric.createLossMetric(0));
        return true;
     }

    /**
     * 该方法用于更新变换后的数据
     * @param data
     * @param optimum
     * @param kcol
     * @param tcol
     * @return
     */
     private static Boolean updataData(ArrayList<ArrayList<String>> data,
                                       DataHandle optimum,
                                       ArrayList<Integer> kcol,
                                       ArrayList<Integer> tcol
                                       ){
         ArrayList<Integer> col= (ArrayList<Integer>) kcol.clone();
         col.addAll(tcol);
         Iterator<String[]> transformed = optimum.iterator();
         int row=0;
         while (transformed.hasNext()) {
             String[] strings=transformed.next();
             for(int i=0;i<col.size();i++){
                 data.get(col.get(i)).set(row,strings[i]);
             }
             row++;
         }
         return true;
     }
}
