package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import com.CLD.dataAnonymization.util.deidentifier.resources.ResourcesRead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @description:该类用于对地理信息进行限制
 * @Author CLD
 * @Date 2018/8/3 12:12
 */
public class Geography {

    public static Boolean geographyHandle(ArrayList<ArrayList<String>> data,
                                          ArrayList<Integer> col,
                                          ArrayList<ArrayList<HashMap<String,String>>> proInfo,
                                          int level){
        if(col.size()==0) return true;
        if(level!=1 && level!=2) level=2;
        String value;
        Pattern pattern=Pattern.compile("[0-9]+");//判断是否为邮编
        try {
            // 地址分类信息加载
            ArrayList<ArrayList<String>> Geo= ResourcesRead.readAddress();
            ArrayList<String> address=new ArrayList<String>();
            address.addAll(Geo.get(0));
            if(level==1) address.addAll(Geo.get(1));

            // 地址信息处理
            for(int i=0;i<col.size();i++){
                int colume=col.get(i);
                for(int j=0;j<data.get(0).size();j++){
                    if ((data.get(colume).get(j) == null) || (data.get(colume).get(j).equals(""))) continue;
                    if(!pattern.matcher(data.get(colume).get(j)).matches()){
                        value="";
                        for(int k=0;k<address.size();k++){
                            if(data.get(colume).get(j).indexOf(address.get(k))!=-1)
                                value+=address.get(k);
                        }
                    }else{
                        value=data.get(colume).get(j).substring(0,4)+"**";
                    }
                    HashMap<String,String> info=new HashMap<String,String>();
                    info.put(new String(data.get(colume).get(j)),value);
                    proInfo.get(j).add(info);
                    data.get(colume).set(j,value);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
