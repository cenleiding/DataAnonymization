package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @description: 将指定的列进行抑制处理
 * @Author CLD
 * @Date 2018/8/2 17:36
 */
public class Suppression {

    /**
     * 对二维链表的指定列进行抑制处理，并保存处理字段
     * @param data     二维链表数据
     * @param col      处理的列坐标
     * @param proInfo  处理字段信息
     * @return
     */
    public static Boolean suppressionHandle(ArrayList<ArrayList<String>> data,
                                            ArrayList<Integer> col,
                                            ArrayList<HashMap<String,String>> proInfo){
        if (col.size()==0) return true;
        try{
            for(int column : col){
                for(int j=1;j<data.get(column).size();j++){
                    proInfo.get(j).put(new String(data.get(column).get(j)),"***");
                    data.get(column).set(j,"***");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
