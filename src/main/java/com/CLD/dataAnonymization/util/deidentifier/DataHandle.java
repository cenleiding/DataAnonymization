package com.CLD.dataAnonymization.util.deidentifier;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @description:该类用于存储处理数据、泛化层次结构、地理信息、字段信息，和进行基本操作
 * @Author CLD
 * @Date 2018/8/6 13:58
 */
public class DataHandle {

    //数据
    private ArrayList<ArrayList<String>>                 data             =       null;
    //标题行
    private String[]                                     header           =       null;
    //泛化层次结构
    private HashMap<String,ArrayList<ArrayList<String>>> hierarchy        =       null;
    //地理信息
    private HashMap<String,ArrayList<String>>            geographic       =       null;
    //字段分类总表
    private ArrayList<ArrayList<String>>                 fieldList        =       null;

    private HashSet<String>                              EI               =       null;

    private HashSet<String>                              QI_Geographic    =       null;

    private HashSet<String>                              QI_DateRecord    =       null;

    private HashSet<String>                              QI_DateAge       =       null;

    private HashSet<String>                              QI_Link          =       null;

    private HashSet<String>                              QI_String        =       null;

    private HashSet<String>                              QI_Number        =       null;

    private HashSet<String>                              SI_String        =       null;

    private HashSet<String>                              SI_Number        =       null;


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
        setHeader();
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
        setHeader();
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
        setHeader();
    }

    /**
     * 构造器
     * @param data
     */
    public DataHandle(ArrayList<ArrayList<String>> data){
        setData(data);
        dataClear();
        setHeader();
    }

    /**
     * 构造器
     * @param resultSet
     */
    public DataHandle(ResultSet resultSet){
        try {
            this.data=new ArrayList<ArrayList<String>>();
            ResultSetMetaData rsmd=resultSet.getMetaData();
            while (resultSet.next()){
                ArrayList<String> row=new ArrayList<String>();
                for(int i=1;i<=rsmd.getColumnCount();i++)
                    row.add(resultSet.getString(i));
                this.data.add(row);
            }
            dataTranspose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 进行数据清洗，将null值，空值转换为""
     * 对数据进行补全形成矩阵
     * 将全空行，列移除
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

        //清理空行列
        for(int t=0;t<2;t++){
            for(int i=0;i<data.size();i++){
                Boolean sign=true;
                for(int j=0;j<data.get(i).size();j++)
                    if(!data.get(i).get(j).equals("")) sign=false;
                if(sign) data.remove(i);
            }
            dataTranspose();
        }

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
        setHeader();
        return true;
    }

    public void setFieldList(ArrayList<ArrayList<String>> fieldList) {
        this.fieldList = fieldList;
        fieldStandard();
        classifyFieldList();
    }

    /**
     * 用于对字段列表进行分类存储
     * @return
     */
    private Boolean classifyFieldList(){
        EI              =        new HashSet<String>();
        QI_DateAge      =        new HashSet<String>();
        QI_DateRecord   =        new HashSet<String>();
        QI_Geographic   =        new HashSet<String>();
        QI_Link         =        new HashSet<String>();
        QI_Number       =        new HashSet<String>();
        QI_String       =        new HashSet<String>();
        SI_Number       =        new HashSet<String>();
        SI_String       =        new HashSet<String>();
        for(int i=0;i<fieldList.size();i++){
            if(fieldList.get(i).get(1).equals("EI"))               EI.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_DateAge"))       QI_DateAge.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_DateRecord"))    QI_DateRecord.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Geography"))    QI_Geographic.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Link"))          QI_Link.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_Number"))        QI_Number.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("QI_String"))        QI_String.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("SI_Number"))        SI_Number.add(fieldList.get(i).get(0));
            if(fieldList.get(i).get(1).equals("SI_String"))        SI_String.add(fieldList.get(i).get(0));
        }
        return true;
    }

    /**
     * 该方法用于生成数据头
     * @return
     */
    private Boolean setHeader(){
        this.header=new String[data.size()];
        for(int i=0;i<data.size();i++)
            header[i]=data.get(i).get(0);

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

    public void setGeographic(HashMap<String,ArrayList<String>> geographic) {
        this.geographic = geographic;
    }

    public void setGeographic(ArrayList<ArrayList<String>> geographic) {
        this.geographic=new HashMap<String,ArrayList<String>>();
        this.geographic.put("bigCity",geographic.get(0));
        this.geographic.put("smallCity",geographic.get(1));
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

    public void addHierarchy(String name,ArrayList<ArrayList<String>> h){
        if (hierarchy==null) hierarchy=new HashMap<String, ArrayList<ArrayList<String>>>();
        hierarchy.put(name,h);
    }

    /**
     * 用于将字段统一化
     * @return
     */
    private void fieldStandard(){
        for(int i=0;i<fieldList.size();i++){
            fieldList.get(i).set(0,fieldList.get(i).get(0)
                    .toLowerCase()
                    .replace(".","")
                    .replace("_","")
                    .replace("-","")
                    .replace("*",""));
        }
    }

    public ArrayList<ArrayList<String>> getFieldList() {
        return fieldList;
    }

    public HashSet<String> getEI() {
        return EI;
    }

    public HashSet<String> getQI_Geographic() {
        return QI_Geographic;
    }

    public HashSet<String> getQI_DateRecord() {
        return QI_DateRecord;
    }

    public HashSet<String> getQI_DateAge() {
        return QI_DateAge;
    }

    public HashSet<String> getQI_Link() {
        return QI_Link;
    }

    public HashSet<String> getQI_String() {
        return QI_String;
    }

    public HashSet<String> getQI_Number() {
        return QI_Number;
    }

    public HashSet<String> getSI_String() {
        return SI_String;
    }

    public HashSet<String> getSI_Number() {
        return SI_Number;
    }

    public HashMap<String,ArrayList<String>> getGeographic() {
        return geographic;
    }

    public String[] getHeader() {
        return header;
    }
}