package com.CLD.dataAnonymization.util.deidentifier;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于处理非结构化数据
 * @Author CLD
 * @Date 2018/4/12 19:55
 **/
public class Unstructured {

    /**
     * @param data           待处理数据
     * @param info           已知可处理字段
     * @param Geographic     地理限制范围
     * @return
     */
    public static String identity(String data, ArrayList<String> info,ArrayList<String>Geographic){
        Pattern p=null;
        Matcher m=null;

        //姓名
        p=Pattern.compile("姓\\s*名\\s*[：|:]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.add(m1.group().replace(" ", ""));
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
            info.add(m1.group().replace(" ", ""));
            data=data.replace(m.group(), "住 院 号: *****");
        }
        //医师签名
        p=Pattern.compile("医\\s*师\\s*签\\s*名\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.add(m1.group().replace(" ", ""));
            data=data.replace(m.group(), "医 师 签 名: ***");
        }
        //主治医师
        p=Pattern.compile("主\\s*治\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.add(m1.group().replace(" ", ""));
            data=data.replace(m.group(), "主 治 医 师: ***");
        }
        //住院医师
        p=Pattern.compile("住\\s*院\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.add(m1.group().replace(" ", ""));
            data=data.replace(m.group(), "住 院 医 师: ***");
        }
        //责任护师
        p=Pattern.compile("责\\s*任\\s*护\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.add(m1.group().replace(" ", ""));
            data=data.replace(m.group(), "责 任 护 师: ***");
        }
        //上级医师
        p=Pattern.compile("上\\s*级\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.add(m1.group().replace(" ", ""));
            data=data.replace(m.group(), "上 级 医 师: ***");
        }
        //主管医师
        p=Pattern.compile("主\\s*管\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.add(m1.group().replace(" ", ""));
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
            for(int k=0;k<Geographic.size();k++){
                if(m1.group().indexOf(Geographic.get(k))!=-1)
                    value+=Geographic.get(k);
            }
            data=data.replace(m.group(),  "出 生 地 ："+value);
        }

        //移除历史信息
        for (int i = 0; i < info.size() ; i++) {
            if (!info.get(i).equals(""))
            data=data.replace(info.get(i),"***");
        }
        return data;
    }

}
