package com.CLD.dataAnonymization.util.deidentifier;

import com.CLD.dataAnonymization.util.deidentifier.algorithm.*;
import com.CLD.dataAnonymization.util.deidentifier.resources.ResourcesReader;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * @description:该类根据 DataHandle 和 Configuration.运行相应算法，进行匿名处理。
 * @Author CLD
 * @Date 2018/8/6 16:34
 */
public class Anonymizer {

    private DataHandle dataHandle;

    private Configuration configuration;

    public Anonymizer(DataHandle dataHandle){
        this.dataHandle=dataHandle;
        this.configuration=new Configuration();
    }

    public Anonymizer(DataHandle dataHandle, Configuration configuration){
        this.dataHandle=dataHandle;
        this.configuration=configuration;
    }

    /**
     * 匿名化驱动
     * @return
     */
    public ArrayList<ArrayList<String>> anonymize(){
        try{
            dataHandle.dataTranspose();
            dataArrange();
            if(configuration.getLevel()==Configuration.AnonymousLevel.Level1)
                runLevel_1();
            if(configuration.getLevel()==Configuration.AnonymousLevel.Level2)
                runLevel_2();
            dataHandle.dataTranspose();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return dataHandle.getData();
    }

    /**
     * 该方法用于对datahandle缺失项进行填充
     * 检查 hierarchy,geographic,fieldList数据项
     * 并对时间数据进行整理
     * @return
     */
    private Boolean dataArrange() throws FileNotFoundException {
        //加载默认地理信息
        if(dataHandle.getGeographic()==null){
            dataHandle.setGeographic(ResourcesReader.readAddress());
        }

        //加载默认字段分类表
        if(dataHandle.getFieldList()==null){
            dataHandle.setFieldList(ResourcesReader.readFields());
        }

        //加载默认字典
        if(dataHandle.getDictionary()==null){
            dataHandle.setDictionary(ResourcesReader.readDictionary());
        }

        //加载默认规则
        if(dataHandle.getRegular()==null){
            dataHandle.setRegular(ResourcesReader.readRegular());
        }

        //时间信息格式化
        dataClear();

        //数值信息格式化
        numberClear();

        //泛化层次构建
        HierarchyBuilder.checkHierarchy(dataHandle);
        return true;
    }

    /**
     * 对数据
     * 以等级1的规范来处理
     * @return
     */
    private Boolean runLevel_1(){
        ArrayList<HashMap<String,String>> proInfo=new ArrayList<HashMap<String,String>>();
        for(int i=0;i<dataHandle.getData().get(0).size();i++)
            proInfo.add(new HashMap<String,String>());
        ArrayList<Integer> column=null;
        ArrayList<Integer> k_col=new ArrayList<Integer>();
        ArrayList<Integer> t_col=new ArrayList<Integer>();
        //处理EI信息
        column=selectTypeColumn(dataHandle.getEI());
        Suppression.suppressionHandle(dataHandle.getData(),column,proInfo);

        //处理加密信息
        column=selectTypeColumn(dataHandle.getQI_Link());
        Encrypt.encryptHandle(dataHandle.getData(),column,configuration.getEncryptPassword(),proInfo);

        //处理地址信息
        column=selectTypeColumn(dataHandle.getQI_Geographic());
        Geography.geographyHandle(dataHandle.getData(),column,dataHandle.getGeographic(),proInfo, Geography.GeographyLevel.SmallCity);

        //处理关于记录的时间信息
        column=selectTypeColumn(dataHandle.getQI_DateRecord());
        Datetime.datetimeHandle(dataHandle.getData(),column,Datetime.DatetimeLevel.DAY);

        //处理关于年龄的时间信息
        column=selectTypeColumn(dataHandle.getQI_DateAge());
        Datetime.datetimeHandle(dataHandle.getData(),column,Datetime.DatetimeLevel.YEAR);

        //处理准标识符的数据信息
        column=selectTypeColumn(dataHandle.getQI_Number());
        LaplaceNoise.laplaceNoiseHandle(dataHandle.getData(),column,configuration.getNoiseScope_small());

        //处理准标识符的字符串信息
        k_col=selectTypeColumn(dataHandle.getQI_String());
        Tcloseness.tClosenessHandle(dataHandle.getData(),k_col,t_col,configuration.getK_small(),
                configuration.getT(),configuration.getSuppressionLimit_level1(),dataHandle.getHierarchy());

        //SI 敏感信息不处理

        //处理UI非结构化信息
        column=selectTypeColumn(dataHandle.getUI());
        Unstructured.unstructuredHandle(dataHandle.getData(),column,proInfo,dataHandle.getDictionary(),dataHandle.getRegular());

        return true;
    }

    /**
     * 对数据
     * 以等级2的规范来执行
     * @return
     */
    private Boolean runLevel_2(){
        ArrayList<HashMap<String,String>> proInfo=new ArrayList<HashMap<String,String>>();
        for(int i=0;i<dataHandle.getData().get(0).size();i++)
            proInfo.add(new HashMap<String,String>());
        ArrayList<Integer> column=null;
        ArrayList<Integer> k_col=new ArrayList<Integer>();
        ArrayList<Integer> t_col=new ArrayList<Integer>();
        //处理EI信息
        column=selectTypeColumn(dataHandle.getEI());
        Suppression.suppressionHandle(dataHandle.getData(),column,proInfo);

        //处理加密信息
        column=selectTypeColumn(dataHandle.getQI_Link());
        Encrypt.encryptHandle(dataHandle.getData(),column,configuration.getEncryptPassword(),proInfo);

        //处理地址信息
        column=selectTypeColumn(dataHandle.getQI_Geographic());
        Geography.geographyHandle(dataHandle.getData(),column,dataHandle.getGeographic(),proInfo, Geography.GeographyLevel.BigCity);

        //处理关于记录的时间信息
        k_col=selectTypeColumn(dataHandle.getQI_DateRecord());

        //处理关于年龄的时间信息
        column=selectTypeColumn(dataHandle.getQI_DateAge());
        Datetime.datetimeHandle(dataHandle.getData(),column,Datetime.DatetimeLevel.NOISE);

        //处理准标识符的数据信息
        column=selectTypeColumn(dataHandle.getQI_Number());
        LaplaceNoise.laplaceNoiseHandle(dataHandle.getData(),column,configuration.getNoiseScope_big());

        //处理准标识符的字符串信息
        k_col.addAll(selectTypeColumn(dataHandle.getQI_String()));

        //处理敏感信息的数据信息
        column=selectTypeColumn(dataHandle.getSI_Number());
        Microaggregation.microaggregationHandle(dataHandle.getData(),column,configuration.getMicroaggregation());

        //处理敏感信息的字符串信息
        t_col=selectTypeColumn(dataHandle.getSI_String());
        Tcloseness.tClosenessHandle(dataHandle.getData(),k_col,t_col,configuration.getK_big(),
                configuration.getT(),configuration.getSuppressionLimit_level2(),dataHandle.getHierarchy());

        //处理UI非结构化信息
        column=selectTypeColumn(dataHandle.getUI());
        Unstructured.unstructuredHandle(dataHandle.getData(),column,proInfo,dataHandle.getDictionary(),dataHandle.getRegular());

        return true;
    }

    private ArrayList<Integer> selectTypeColumn(HashSet<String> type){
        ArrayList<Integer> col=new ArrayList<Integer>();
        for(int i=0;i<dataHandle.getData().size();i++){
            String h=dataHandle.getData().get(i).get(0)
                    .toLowerCase()
                    .replace(".","")
                    .replace("_","")
                    .replace("-","")
                    .replace("*","");
            if(type.contains(h)) col.add(i);
        }
        return col;
    }


    /**
     * 该方法对数值型数据进行清洗
     * 数值必须能被double化
     *
     * 否则统一为""
     * @return
     */
    private Boolean numberClear(){
        ArrayList<ArrayList<String>> data             =  dataHandle.getData();
        HashSet<String>              QI_Number        =  dataHandle.getQI_Number();
        HashSet<String>              SI_Number        =  dataHandle.getSI_Number();
        for(int i=0;i<data.size();i++) {
            String h = data.get(i).get(0).toLowerCase()
                    .replace(".", "")
                    .replace("_", "")
                    .replace("-", "")
                    .replace("*", "");
            if(QI_Number.contains(h) || SI_Number.contains(h)){
                for(int j=1;j<data.get(i).size();j++){
                    try{
                        Double d=Double.valueOf(data.get(i).get(j));
                    }catch (Exception e){
                        data.get(i).set(j,"");
                    }
                }
            }
        }

        return true;
    }

    /**
     * 该方法对时间进行清洗
     * 时间信息格式必须满足：
     * 时间戳形式，或
     * 包含年月日信息，并用'/'或'-'或'年月日' 分隔，之间没有空格
     * 所包含的时分秒信息必须与年月日信息用空格分隔。
     * yyyy[/|-|年]MM[/|-|月]dd [HH:mm:ss]
     *
     * 将统一为 yyyy-MM-dd 格式，并将不符合规范的值赋为""
     * @return
     */
    private Boolean dataClear(){
        ArrayList<ArrayList<String>> data             =  dataHandle.getData();
        HashSet<String>              QI_DateRecord    =  dataHandle.getQI_DateRecord();
        HashSet<String>              QI_DateAge       =  dataHandle.getQI_DateAge();
        Pattern pattern = null;
        for(int i=0;i<data.size();i++) {
            String h = data.get(i).get(0).toLowerCase()
                    .replace(".", "")
                    .replace("_", "")
                    .replace("-", "")
                    .replace("*", "");
            if(QI_DateAge.contains(h) || QI_DateRecord.contains(h)){
                pattern=Pattern.compile("^[-\\+]?[\\d]*[\\.]?[\\d]*$");
                for(int j=1;j<data.get(i).size();j++){
                    if ((data.get(i).get(j)==null)||(data.get(i).get(j).equals(""))) continue;
                    if (pattern.matcher(data.get(i).get(j)).matches()){//判断是否为xls时间戳形式
                        String[] t=data.get(i).get(j).split("\\.");
                        String format = "yyyy-MM-dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(format);
                        data.get(i).set(j,sdf.format(new Date((long)(((Double.valueOf(t[0])-70*365-19)*86400-8*3600)*1000))));
                    }
                    data.get(i).set(j, data.get(i).get(j)
                            .split(" ")[0]
                            .replace("\\","-")
                            .replace("/","-")
                            .replace("年","-")
                            .replace("月","-")
                            .replace("日",""));
                }
                pattern=Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");
                for(int j=1;j<data.get(i).size();j++){
                    if((data.get(i).get(j)==null) || !pattern.matcher(data.get(i).get(j)).matches())
                        data.get(i).set(j,"");
                }
            }
        }
        return true;
    }

    public DataHandle getDataHandle() {
        return dataHandle;
    }

    public void setDataHandle(DataHandle dataHandle) {
        this.dataHandle = dataHandle;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
