package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @description:该类用于对地理信息进行限制
 * @Author CLD
 * @Date 2018/8/3 12:12
 */
public class Geography {

    public enum GeographyLevel{
        BigCity,SmallCity
    }

    public static Boolean geographyHandle(ArrayList<ArrayList<String>> data,
                                          ArrayList<Integer> col,
                                          HashMap<String,ArrayList<String>> geography,
                                          ArrayList<HashMap<String,String>> proInfo,
                                          GeographyLevel level){
        if(col.size()==0) return true;
        String value;
        Pattern pattern=Pattern.compile("[0-9]+");//判断是否为邮编
        try {
            // 地址分类信息加载
            ArrayList<String> address=new ArrayList<String>();
            address.addAll(geography.get("bigCity"));
            if(level==GeographyLevel.SmallCity) address.addAll(geography.get("smallCity"));

            // 地址信息处理
            for(int column : col){
                for(int j=1;j<data.get(column).size();j++){
                    if ((data.get(column).get(j) == null) || (data.get(column).get(j).equals(""))) continue;
                    if(!pattern.matcher(data.get(column).get(j)).matches()){
                        value="";
                        for(int k=0;k<address.size();k++){
                            if(data.get(column).get(j).indexOf(address.get(k))!=-1)
                                value+=address.get(k);
                        }
                    }else{
                        value=data.get(column).get(j).substring(0,4)+"**";
                    }
                    proInfo.get(j).put(new String(data.get(column).get(j)),value);
                    data.get(column).set(j,value);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
