package com.CLD.dataAnonymization.util.deidentifier;

import java.util.*;

/**
 * @description:该类用于存储处理数据、泛化层次结构、地理信息、字段信息，和进行基本操作
 * @Author CLD
 * @Date 2018/8/6 13:58
 */
public class DataHandle {

    //数据
    private ArrayList<ArrayList<String>>                 data             =       null;
    //泛化层次结构
    private HashMap<String,ArrayList<ArrayList<String>>> hierarchy        =       null;
    //地理信息
    private ArrayList<ArrayList<String>>                 Geographic       =       null;
    //字段分类总表
    private ArrayList<ArrayList<String>>                 fieldList        =       null;

    private ArrayList<String>                            EI               =       null;

    private ArrayList<String>                            QI_Geographic    =       null;

    private ArrayList<String>                            QI_DateRecord    =       null;

    private ArrayList<String>                            QI_DateAge       =       null;

    private ArrayList<String>                            QI_Link          =       null;

    private ArrayList<String>                            QI_String        =       null;

    private ArrayList<String>                            QI_Number        =       null;

    private ArrayList<String>                            SI_String        =       null;

    private ArrayList<String>                            SI_Number        =       null;


    /**
     * 构造器
     * @param data
     */
    public DataHandle(String[][] data){
        this.data=new ArrayList<ArrayList<String>>();
        for(int col=0;col<data[0].length;col++){
            ArrayList<String> column=new ArrayList<String>();
            for(int row=0;row<data.length;row++){
                column.add(data[row][col]);
            }
            this.data.add(column);
        }
        dataClear();
    }

    /**
     * 构造器
     * @param data
     */
    public DataHandle(List<String[]> data){
        this.data=new ArrayList<ArrayList<String>>();
        for(int i=0;i<data.size();i++){
            ArrayList<String> column=new ArrayList<String>();
            for(String s:data.get(i))
                column.add(s);
            this.data.add(column);
        }
        dataClear();
    }

    /**
     * 构造器
     * @param data
     */
    public DataHandle(Iterator<String[]> data){
        this.data=new ArrayList<ArrayList<String>>();
        while(data.hasNext()){
            ArrayList<String> column=new ArrayList<String>();
            String[] strings=data.next();
            for(String s:strings)
                column.add(s);
            this.data.add(column);
        }
        dataClear();
    }

    /**
     * 构造器
     * @param data
     */
    public DataHandle(ArrayList<ArrayList<String>> data){
        setData(data);
        dataClear();
    }

    /**
     * 进行数据清洗，将null值，空值转换为""
     * 对数据进行补全形成矩阵
     * @return
     */
    public Boolean dataClear(){
        //方阵补全
        int max=0;
        for(int i=0;i<data.size();i++)
            max=max<data.get(i).size()?data.get(i).size():max;
        for(int i=0;i<data.size();i++)
            while(data.get(i).size()<max) data.get(i).add("");

        //脏值转换
        for(int i=0;i<data.size();i++)
            for(int j=0;j<data.get(i).size();j++)
                if(data.get(i).get(j)==null ||
                        data.get(i).get(j).toLowerCase().equals("null") ||
                        data.get(i).get(j).replace(" ","").length()==0)
                    data.get(i).set(j,"");

        return true;
    }

    /**
     * 对数据进行转置处理，使其以列为单位。
     * 是否执行视情况而定
     * @return
     */
    public Boolean  dataTranspose(){
        ArrayList<ArrayList<String>> newData=new ArrayList<ArrayList<String>>();
        for(int i=0;i<data.get(0).size();i++){
            ArrayList<String> column=new ArrayList<String>();
            for(int j=0;j<data.size();j++)
                column.add(data.get(j).get(i));
            newData.add(column);
        }
        data=newData;
        return true;
    }

    public void setFieldList(ArrayList<ArrayList<String>> fieldList) {
        this.fieldList = fieldList;
        classifyFieldList();
    }

    /**
     * 用于对字段列表进行分类存储
     * @return
     */
    public Boolean classifyFieldList(){
        EI              =        new ArrayList<String>();
        QI_DateAge      =        new ArrayList<String>();
        QI_DateRecord   =        new ArrayList<String>();
        QI_Geographic   =        new ArrayList<String>();
        QI_Link         =        new ArrayList<String>();
        QI_Number       =        new ArrayList<String>();
        QI_String       =        new ArrayList<String>();
        SI_Number       =        new ArrayList<String>();
        SI_String       =        new ArrayList<String>();
        for(int i=0;i<fieldList.size();i++){
            if(fieldList.get(i).get(1).equals("EI"))               EI.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_DateAge"))       QI_DateAge.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_DateRecord"))    QI_DateRecord.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Geographic"))    QI_Geographic.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Link"))          QI_Link.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Number"))        QI_Number.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_String"))        QI_String.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("SI_Number"))        SI_Number.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("SI_String"))        SI_String.add(fieldList.get(i).get(0));
        }
        return true;
    }

    public ArrayList<ArrayList<String>> getData() {
        return data;
    }

    public String[][] getDataAsArray2(){
        String [][] outData=new String[data.get(0).size()][data.size()];
        for(int i=0;i<data.get(0).size();i++)
            for(int j=0;j<data.size();j++)
                outData[i][j]=data.get(j).get(i);
        return outData;
    }

    public List<String[]> getDataAsListArray(){
        List<String[]> outData=new ArrayList<String[]>();
        for(int i=0;i<data.get(0).size();i++){
            String[] s=new String[data.size()];
            for(int j=0;j<data.size();j++)
                s[j]=data.get(j).get(i);
            outData.add(s);
        }
        return outData;
    }

    public Iterator<String[]> getDataAsIteratorArray() {
        return new Iterator<String[]>() {

            private int pos = 0;

            @Override
            public boolean hasNext() {
                return pos<data.get(0).size();
            }

            @Override
            public String[] next() throws NoSuchElementException {
                if (hasNext()) {
                    String[] strings=new String[data.size()];
                    for(int i=0;i<data.size();i++)
                        strings[i]=data.get(i).get(pos);
                    pos++;
                    return strings;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void setData(ArrayList<ArrayList<String>> data) {
        this.data = data;
        dataClear();
    }

    public HashMap<String, ArrayList<ArrayList<String>>> getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(HashMap<String, ArrayList<ArrayList<String>>> hierarchy) {
        this.hierarchy = hierarchy;
    }

    public ArrayList<ArrayList<String>> getFieldList() {
        return fieldList;
    }

    public ArrayList<String> getEI() {
        return EI;
    }

    public ArrayList<String> getQI_Geographic() {
        return QI_Geographic;
    }

    public ArrayList<String> getQI_DateRecord() {
        return QI_DateRecord;
    }

    public ArrayList<String> getQI_DateAge() {
        return QI_DateAge;
    }

    public ArrayList<String> getQI_Link() {
        return QI_Link;
    }

    public ArrayList<String> getQI_String() {
        return QI_String;
    }

    public ArrayList<String> getQI_Number() {
        return QI_Number;
    }

    public ArrayList<String> getSI_String() {
        return SI_String;
    }

    public ArrayList<String> getSI_Number() {
        return SI_Number;
    }

    public ArrayList<ArrayList<String>> getGeographic() {
        return Geographic;
    }

    public void setGeographic(ArrayList<ArrayList<String>> geographic) {
        Geographic = geographic;
    }
}