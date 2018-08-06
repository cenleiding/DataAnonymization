package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @description: 该类用于处理时间信息
 * 时间信息格式必须满足：
 * 时间戳形式，或
 * 包含年月日信息，并用'/'或'-'或'年月日' 分隔，之间没有空格
 * 所包含的时分秒信息必须与年月日信息用空格分隔。
 * yyyy[/|-|年]MM[/|-|月]dd [HH:mm:ss]
 *
 * 对于不满足格式的单元格不进行处理
 *
 * @Author CLD
 * @Date 2018/8/3 15:49
 */
public class Datetime {

    public enum DatetimeLevel{
       DAY,YEAR,NOISE
    }

    private static final Double NOISEFLU=0.08d;

    public static Boolean datetimeHandle(ArrayList<ArrayList<String>> data,
                                         ArrayList<Integer> col,
                                         DatetimeLevel datetimeLevel){
            datetimeClear(data,col);
            switch (datetimeLevel){
                case DAY:
                    dayRule(data,col);
                    break;
                case YEAR:
                    yearRule(data,col);
                    break;
                case NOISE:
                    noiseRule(data,col);
                    break;
            }


        return true;
    }

    /**
     * 该方法将时间保留至日信息
     * @param data
     * @param col
     * @return
     */
    private static Boolean dayRule(ArrayList<ArrayList<String>> data,
                                   ArrayList<Integer> col){
        return true;
    }

    /**
     * 该方法将时间保留至年信息
     * @param data
     * @param col
     * @return
     */
    private static Boolean yearRule(ArrayList<ArrayList<String>> data,
                                    ArrayList<Integer> col){
        for(int Column : col ) {
            for(int j=0;j<data.get(0).size();j++){
                String[] t=data.get(Column).get(j).split("/");
                if (t.length==3) data.get(Column).set(j,t[0]);
            }
        }
        return true;
    }

    /**
     * 该方法将时间保留至年信息,
     * 并以13岁和89岁为界限，
     * 并加以laplace噪声
     * @param data
     * @param col
     * @return
     */
    private static Boolean noiseRule(ArrayList<ArrayList<String>> data,
                                     ArrayList<Integer> col){
        for(int Column : col) {
            for(int j=0;j<data.get(0).size();j++){
                String[] t=data.get(Column).get(j).split("/");
                if (t.length==3) {
                    int year= Calendar.getInstance().get(Calendar.YEAR);
                    int y=Integer.parseInt(t[0]);
                    if((year-y)>89) data.get(Column).set(j, "大于90年");
                    else if ((year-y)<14)data.get(Column).set(j, "小于14岁");
                    else data.get(Column).set(j,t[0]);
                }
            }
        }
        LaplaceNoise.laplaceNoiseHandle(data,col,NOISEFLU);

        return true;
    }



    /**
     * 该方法用于转换xls,xlsx的时间戳格式，
     * 并去除时分秒信息,
     * 并统一为 yyyy/MM/dd 格式
     * @return
     */
    private static Boolean datetimeClear(ArrayList<ArrayList<String>> data,
                                         ArrayList<Integer> col){
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*[\\.]?[\\d]*$");
        for(int Column : col){
            for(int j=0;j<data.get(0).size();j++){
                if ((data.get(Column).get(j)==null)||(data.get(Column).get(j).equals(""))) continue;
                if (pattern.matcher(data.get(Column).get(j)).matches()){//判断是否为xls时间戳形式
                    String[] t=data.get(Column).get(j).split("\\.");
                    String format = "yyyy-MM-dd";
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    data.get(Column).set(j,sdf.format(new Date((long)(((Double.valueOf(t[0])-70*365-19)*86400-8*3600)*1000))));
                }
                data.get(Column).set(j, data.get(Column).get(j)
                        .split(" ")[0]
                        .replace("-","/")
                        .replace("年","/")
                        .replace("月","/")
                        .replace("日",""));
            }
        }
        return true;
    }
}
