package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @description: 该类用于处理时间信息
 * 时间必须为yyyy-MM-dd 格式或为 ""
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
        for(int column : col ) {
            for(int j=1;j<data.get(column).size();j++){
                String[] t=data.get(column).get(j).split("-");
                if (t.length==3) data.get(column).set(j,t[0]);
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
        for(int column : col) {
            for(int j=1;j<data.get(column).size();j++){
                String[] t=data.get(column).get(j).split("-");
                if (t.length==3) {
                    int year= Calendar.getInstance().get(Calendar.YEAR);
                    int y=Integer.parseInt(t[0]);
                    if((year-y)>89) data.get(column).set(j, "大于90年");
                    else if ((year-y)<14)data.get(column).set(j, "小于14岁");
                    else data.get(column).set(j,t[0]);
                }
            }
        }
        LaplaceNoise.laplaceNoiseHandle(data,col,NOISEFLU);

        return true;
    }

}
