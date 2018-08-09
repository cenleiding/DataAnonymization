package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于处理非结构化数据
 * @Author CLD
 * @Date 2018/4/12 19:55
 **/
public class Unstructured {


    public static Boolean unstructuredHandle(ArrayList<ArrayList<String>> data,
                                             ArrayList<Integer> col,
                                             ArrayList<HashMap<String,String>> proInfo,
                                             HashMap<String,ArrayList<String>> geographic){
        ArrayList<String> geo=new ArrayList<String>();
        geo.addAll(geographic.get("bigCity"));
        geo.addAll(geographic.get("smallCity"));
        for(int column : col ) {
            for(int j=1;j<data.get(column).size();j++) {
                data.get(column).set(j,identity(data.get(column).get(j),proInfo.get(j),geo));
            }
        }
        return true;
    }

    /**
     * @param data           待处理数据
     * @param info           已知可处理字段
     * @param geographic     地理限制范围
     * @return
     */
    public static String identity(String data, HashMap<String,String> info,ArrayList<String>geographic){
        Pattern p=null;
        Matcher m=null;

        //姓名
        p=Pattern.compile("姓\\s*名\\s*[：|:]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "姓    名: ***");
        }

        //时间
        p=Pattern.compile("\\d{4}\\s*[-|年|/]\\s*\\d{1,2}\\s*[-|月|/]\\s*\\d{1,2}\\s*日?");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\d{4}\\s*[-|年|/]\\s*\\d{1,2}\\s*[-|月|/]");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            data=data.replace(m.group(), m1.group());
        }
        //年龄
        p=Pattern.compile("年\\s*龄\\s*[：|:]\\s*\\d*\\s*岁");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\d+");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            if(Integer.parseInt(m1.group())>90) data=data.replace(m.group(), "年 龄：大于90岁");
        }
        //住院号
        p=Pattern.compile("住\\s*院\\s*号\\s*[:|：]\\s*\\d+");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\d+");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "住 院 号: *****");
        }
        //医师签名
        p=Pattern.compile("医\\s*师\\s*签\\s*名\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "医 师 签 名: ***");
        }
        //主治医师
        p=Pattern.compile("主\\s*治\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "主 治 医 师: ***");
        }
        //住院医师
        p=Pattern.compile("住\\s*院\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "住 院 医 师: ***");
        }
        //责任护师
        p=Pattern.compile("责\\s*任\\s*护\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "责 任 护 师: ***");
        }
        //上级医师
        p=Pattern.compile("上\\s*级\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "上 级 医 师: ***");
        }
        //主管医师
        p=Pattern.compile("主\\s*管\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "主 管 医 师: ***");
        }
        //病房
        p=Pattern.compile("病\\s*房\\s*[:|：]\\S*");
        m=p.matcher(data);
        while(m.find()){
            data=data.replace(m.group(), "病 房: ***");
        }
        //床号
        p=Pattern.compile("床\\s*号\\s*[:|：]\\S*");
        m=p.matcher(data);
        while(m.find()){
            data=data.replace(m.group(), "床 号: ***");
        }
        //出生地
        p=Pattern.compile("出\\s*生\\s*地\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            String value="";
            for(int k=0;k<geographic.size();k++){
                if(m1.group().indexOf(geographic.get(k))!=-1)
                    value+=geographic.get(k);
            }
            data=data.replace(m.group(),  "出 生 地 ："+value);
        }

        //移除历史信息
        for (String inf:info.keySet()) {
            if (!inf.replace(" ","").equals(""))
            data=data.replaceAll(inf,info.get(inf));
        }
        return data;
    }

}
