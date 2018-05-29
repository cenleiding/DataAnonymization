package com.CLD.dataAnonymization.util.deidentifier;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 此类对数据按照HIPPA《安全屋》标准进行处理
 **/
public class Hippa {

    /**
     * 此类对数据按照HIPPA《安全屋》或《有限数据集》标准进行处理
     * 注意：经《有限数据集》处理的数据仍有隐私威胁，不可公开使用！
     * @param type               处理方式。SafeHarbor;LimitedSet
     * @param data               待处理数据
     * @param EI                 显示标识符(Explicit-Identifier) ：直接隐去
     * @param QI_Geographic      准标识符(Quasi-Identifier)-地理 :将缩小到相应范围
     * @param QI_Date            准标识符(Quasi-Identifier)-时间 :SafeHarbor缩小到年信息;LimitedSet缩小到日信息
     * @param QI_Number          准标识符(Quasi-Identifier)-数值型：进行加密处理
     * @param QI_String          准标识符(Quasi-Identifier)-字符串型:SafeHarbor隐去出现频率小于5%的信息;LimitedSet隐去出现频率小于2%的信息
     * @param UI                 非结构化标识符(Unstructured-Identifier):进行非结构化处理
     * @param Geographic         允许出现的地理信息
     * @return                   符合安全屋规则的匿名数据
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<ArrayList<String>> Identity(String type,
                                                        ArrayList<ArrayList<String>>data,
                                                        ArrayList<String> EI,
                                                        ArrayList<String> QI_Geographic,
                                                        ArrayList<String> QI_Date,
                                                        ArrayList<String> QI_Number,
                                                        ArrayList<String> QI_String,
                                                        ArrayList<String> UI,
                                                        ArrayList<String> Geographic){

        if(data.isEmpty()) return data;
        ArrayList<ArrayList<String>> deleteInfo=new ArrayList<ArrayList<String>>();//用于记录删除的信息
        for (int i = 0; i < data.size(); i++) deleteInfo.add(new ArrayList<String>());
        fieldStandard(EI);
        fieldStandard(QI_Geographic);
        fieldStandard(QI_Date);
        fieldStandard(QI_Number);
        fieldStandard(QI_String);
        fieldStandard(UI);

        Set EI_set             =new HashSet(){{addAll(EI);}};
        Set QI_Geographic_set  =new HashSet(){{addAll(QI_Geographic);}};
        Set QI_Date_set        =new HashSet(){{addAll(QI_Date);}};
        Set QI_Number_set      =new HashSet(){{addAll(QI_Number);}};
        Set QI_String_set      =new HashSet(){{addAll(QI_String);}};
        Set UI_set             =new HashSet(){{addAll(UI);}};

        //判断字段类型
        for(int i=0;i<data.get(0).size();i++){
            String headString=data.get(0).get(i).toLowerCase()
                    .replace(".","")
                    .replace("_","")
                    .replace("-","")
                    .replace("*","");
            if(EI_set.contains(headString))            {EIHandle(data,deleteInfo,i);};
            if(QI_Geographic_set.contains(headString)) {QIGeographicHandle(data,deleteInfo,Geographic,i); };
            if(QI_Date_set.contains(headString))       {QIDateHandle(data,deleteInfo,type,i); };
            if(QI_Number_set.contains(headString))     {QINumberHandle(data,deleteInfo,i); };
            if(QI_String_set.contains(headString))     {QIStringHandle(data,deleteInfo,type,i); };
            if(UI_set.contains(headString))            {UIHandle(data,deleteInfo,Geographic,i); };
        }

        return data;
    }

    /**
     * 处理EI数据：直接移除数据
     * @param data
     * @param deleteInfo
     * @param col
     */
    private static void EIHandle(ArrayList<ArrayList<String>> data,
                                 ArrayList<ArrayList<String>>deleteInfo,
                                 int col){
        for(int i=1;i<data.size();i++){
            deleteInfo.get(i).add(data.get(i).get(col));
            data.get(i).set(col,"***");
        }
    }

    /**
     * 处理QI_Geographic数据：根据地理大小进行处理
     * @param data
     * @param deleteInfo
     * @param Geographic
     * @param col
     */
    private static void QIGeographicHandle(ArrayList<ArrayList<String>> data,
                                           ArrayList<ArrayList<String>>deleteInfo,
                                           ArrayList<String>Geographic,
                                           int col){
        String value;
        Pattern pattern=Pattern.compile("[0-9]+");//判断是否为邮编
        for(int i=1;i<data.size();i++) {
            if ((data.get(i).get(col) == null) || (data.get(i).get(col).equals(""))) continue;
            if(!pattern.matcher(data.get(i).get(col)).matches()){
                value="";
                for(int k=0;k<Geographic.size();k++){
                    if(data.get(i).get(col).indexOf(Geographic.get(k))!=-1)
                        value+=Geographic.get(k);
                }
            }else{
                value=data.get(i).get(col).substring(0,4)+"**";
            }
            data.get(i).set(col,value);
        }
    }

    /**
     * 处理QI_Date数据：SafeHarbor缩小到年信息;LimitedSet缩小到日信息
     * @param data
     * @param deleteInfo
     * @param col
     * @param type
     */
    private static void QIDateHandle(ArrayList<ArrayList<String>> data,
                                     ArrayList<ArrayList<String>>deleteInfo,
                                     String type,
                                     int col){
        int year= Calendar.getInstance().get(Calendar.YEAR);
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*[\\.]?[\\d]*$");
        for(int i=1;i<data.size();i++){
            if ((data.get(i).get(col)==null)||(data.get(i).get(col).equals(""))) continue;
            if (pattern.matcher(data.get(i).get(col)).matches()){//判断是否为xls时间戳形式
                String[] t=data.get(i).get(col).split("\\.");
                String format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                data.get(i).set(col,sdf.format(new Date((long)(((Double.valueOf(t[0])-70*365-19)*86400-8*3600)*1000))));
            }

            if(type.equals("SafeHarbor")){
                String[] time = null;
                if(data.get(i).get(col).indexOf("-")!=-1)
                    time=data.get(i).get(col).split("-");
                else if(data.get(i).get(col).indexOf("/")!=-1)
                    time=data.get(i).get(col).split("/");
                else if(data.get(i).get(col).indexOf("年")!=-1)
                    time=data.get(i).get(col).split("年");
                else continue;
                int y=Integer.parseInt(time[0]);
                if((year-y)>89) data.get(i).set(col, "大于90年");
                else data.get(i).set(col, time[0]);
            }

            if(type.equals("LimitedSet")){
                String[] time = null;
                if(data.get(i).get(col).indexOf("-")!=-1)
                    time=data.get(i).get(col).split(" ")[0].split("-");
                if(data.get(i).get(col).indexOf("/")!=-1)
                    time=data.get(i).get(col).split(" ")[0].split("/");
                if(data.get(i).get(col).indexOf("日")!=-1)
                    time=data.get(i).get(col).split("日")[0].split("[\\u4E00-\\u9FA5]");
                if(time.length!=3) continue;
                data.get(i).set(col, time[0]+"/"+time[1]+"/"+time[2]);
            }

        }
    }

    /**
     * 处理QI_String数据：SafeHarbor隐去出现频率小于5%的信息;LimitedSet隐去出现频率小于2%的信息
     * @param data
     * @param deleteInfo
     * @param col
     */
    private static void QIStringHandle(ArrayList<ArrayList<String>> data,
                                       ArrayList<ArrayList<String>>deleteInfo,
                                       String type,
                                       int col){
        Map<String,Integer> map=new HashMap<String,Integer>();
        int num=0;
        double pre=0;
        if(type.equals("SafeHarbor")) pre=0.05;
        else pre=0.02;

        for(int i=1;i<data.size();i++){
            if ((data.get(i).get(col) != null) && (!data.get(i).get(col).equals("")))
                if(map.keySet().contains(data.get(i).get(col)))
                    map.put(data.get(i).get(col),map.get(data.get(i).get(col))+1);
                else map.put(data.get(i).get(col),1);
        }
        for(String s:map.keySet()) num+=map.get(s);
        for(int j=1;j<data.size();j++){
            if ((data.get(j).get(col) != null) && (!data.get(j).get(col).equals(""))){
                if(map.get(data.get(j).get(col))/num<pre){
                    deleteInfo.get(j).add(data.get(j).get(col));
                    data.get(j).set(col,"***");
                }
            }
        }
    }

    /**
     * 处理QI_Number数据：进行3DES加密
     * @param data
     * @param deleteInfo
     * @param col
     */
    private static void QINumberHandle(ArrayList<ArrayList<String>> data,
                                 ArrayList<ArrayList<String>>deleteInfo,
                                 int col){
        for(int i=1;i<data.size();i++){
            deleteInfo.get(i).add(data.get(i).get(col));
            data.get(i).set(col,EncryptUtil.encryptMode(data.get(i).get(col)));
        }
    }

    /**
     * 处理UI数据：非结构化数据
     * @param data
     * @param deleteInfo
     * @param Geographic
     * @param col
     */
    private static void UIHandle(ArrayList<ArrayList<String>> data,
                                 ArrayList<ArrayList<String>>deleteInfo,
                                 ArrayList<String> Geographic,
                                 int col){
        for(int i=1;i<data.size();i++){
            if ((data.get(i).get(col) != null) && (!data.get(i).get(col).equals("")))
                data.get(i).set(col,Unstructured.identity(data.get(i).get(col),deleteInfo.get(i),Geographic));
        }
    }


    /**
     * 用于将字段统一化
     * @param list
     * @return
     */
    private static void fieldStandard(ArrayList<String> list){
        for(int i=0;i<list.size();i++)
            list.set(i, list.get(i).toLowerCase()
                    .replace(".","")
                    .replace("_","")
                    .replace("-","")
                    .replace("*",""));
    }
}
