package com.CLD.dataAnonymization.util.deidentifier;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 此类对数据按照《安全屋》标准进行处理
 * @Author CLD
 * @Date 2018/4/12 10:36
 **/
public class SafeHarbor {

    /**
     * @param data               待处理数据
     * @param removeField        直接移除类字段
     * @param dateField          时间类型字段
     * @param geographicField    地理类型字段
     * @param middleField        按出现频率处理字段
     * @param unstructuredField  非结构化类型字段
     * @return
     */
    public ArrayList<ArrayList<String>> identity(
            ArrayList<ArrayList<String>>data,ArrayList<String> removeField,
            ArrayList<String> dateField,ArrayList<String> geographicField,
            ArrayList<String> middleField,ArrayList<String> unstructuredField ) throws FileNotFoundException, UnsupportedEncodingException {
        if(data.isEmpty()) return data;
        ArrayList<ArrayList<String>> deleteInfo=new ArrayList<ArrayList<String>>();//用于记录删除的信息
        for (int i = 0; i < data.size(); i++) deleteInfo.add(new ArrayList<String>());
        Set set=new HashSet();
        //直接移除数据
        set.addAll(removeField);
        for(int i=0;i<data.get(0).size();i++){
            if(set.contains(data.get(0).get(i).toLowerCase()
                    .replace("_","")
                    .replace("-","_"))){
                for(int j=1;j<data.size();j++){
                    deleteInfo.get(j).add(data.get(j).get(i));
                    data.get(j).set(i,"***");
                }
            }
        }

        //处理时间数据
        set.clear();
        set.addAll(dateField);
        int year=Calendar.getInstance().get(Calendar.YEAR);
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*[\\.]?[\\d]*$");
        String format = "yyyy-MM-dd";
        for(int i=0;i<data.get(0).size();i++){
            if(set.contains(data.get(0).get(i).toLowerCase()
                    .replace("_","")
                    .replace("-","_"))){
                for(int j=1;j<data.size();j++){
                    if ((data.get(j).get(i)==null)||(data.get(j).get(i).equals(""))) continue;
                    if (pattern.matcher(data.get(j).get(i)).matches()){//判断是否为时间戳
                        String[] t=data.get(j).get(i).split("\\.");
                        int o=Integer.parseInt(t[0])-1;//-1为了修正xls的错误
                        int n=1900;
                        int y=1;
                        int r=0;
                        int[] p={31,28,31,30,31,30,31,31,30,31,30,31};
                        while(true){
                            if(((n%4==0)&&(n%100!=0))||(n%400==0)){
                                if(366>=o) break;
                                n++;
                                o=o-366;
                            }
                            else {
                                if(365>=o) break;
                                n++;
                                o=o-365;
                            }
                        }
                        while(true){
                            int w=0;
                            if((((n%4==0)&&(n%100!=0))||(n%400==0))&&(y==2)) w=29;
                            else w=p[y-1];
                            if(w>=o) break;
                            y++;
                            o=o-w;
                        }
                        r=o;
                        data.get(j).set(i,n+"-"+y+"-"+r);
                    }

                    String[] time = null;
                    if(data.get(j).get(i).indexOf("-")!=-1)
                        time=data.get(j).get(i).split("-");
                    else if(data.get(j).get(i).indexOf("/")!=-1)
                        time=data.get(j).get(i).split("/");
                    else if(data.get(j).get(i).indexOf("年")!=-1)
                        time=data.get(j).get(i).split("年");
                    else continue;

                    int y=Integer.parseInt(time[0]);
                    if((year-y)>89) data.get(j).set(i, "大于90年");
                    else data.get(j).set(i, time[0]);
                }
            }
        }

        //处理地理
        set.clear();
        set.addAll(geographicField);
        JSONObject jsonObject=FieldHandle.readAddress();
        JSONArray address=jsonObject.getJSONArray("BigCity");
        String value;
        pattern=Pattern.compile("[0-9]+");//判断是否为邮编
        for(int i=0;i<data.get(0).size();i++){
            if(set.contains(data.get(0).get(i).toLowerCase()
                    .replace("_","")
                    .replace("-","_"))){
                for(int j=1;j<data.size();j++) {
                    if ((data.get(j).get(i) == null) || (data.get(j).get(i).equals(""))) continue;
                    if(!pattern.matcher(data.get(j).get(i)).matches()){
                        value="";
                        for(int k=0;k<address.size();k++){
                            if(data.get(j).get(i).indexOf(address.getString(k))!=-1)
                                value+=address.getString(k);
                        }
                    }else{
                        value=data.get(j).get(i).substring(0,4)+"**";
                    }
                    data.get(j).set(i,value);
                }
            }
        }

        //处理hard_middle信息，将频率小于5%的隐去
        set.clear();
        set.addAll(middleField);
        Map<String,Integer> map=new HashMap<String,Integer>();
        int num;
        for(int i=0;i<data.get(0).size();i++) {
            if (set.contains(data.get(0).get(i).toLowerCase()
                    .replace("_", "")
                    .replace("-", "_"))) {
                map.clear();
                for(int j=1;j<data.size();j++){
                    if ((data.get(j).get(i) != null) && (!data.get(j).get(i).equals("")))
                        if(map.keySet().contains(data.get(j).get(i)))
                            map.put(data.get(j).get(i),map.get(data.get(j).get(i))+1);
                        else map.put(data.get(j).get(i),1);
                }
                num=0;
                for(String s:map.keySet()) num+=map.get(s);
                for(int j=1;j<data.size();j++){
                    if ((data.get(j).get(i) != null) && (!data.get(j).get(i).equals(""))){
                     if(map.get(data.get(j).get(i))/num<0.02){
                         deleteInfo.get(j).add(data.get(j).get(i));
                         data.get(j).set(i,"***");
                     }
                    }
                }
            }
        }

        //处理非结构化数据
        set.clear();
        set.addAll(unstructuredField);
        Unstructured unstructured=new Unstructured();
        for(int i=0;i<data.get(0).size();i++) {
            if (set.contains(data.get(0).get(i).toLowerCase()
                    .replace("_", "")
                    .replace("-", "_"))) {
                for (int j = 0; j <data.size() ; j++) {
                    if ((data.get(j).get(i) != null) && (!data.get(j).get(i).equals("")))
                        data.get(j).set(i,unstructured.identity(data.get(j).get(i),deleteInfo.get(j)));
                }
            }
        }

        return data;
    }




}
