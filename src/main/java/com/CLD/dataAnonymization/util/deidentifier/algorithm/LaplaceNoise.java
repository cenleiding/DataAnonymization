package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import cern.jet.random.Distributions;
import cern.jet.random.engine.RandomEngine;

import java.util.ArrayList;

/**
 * @description: 该类提供Laplace噪声处理
 * @Author CLD
 * @Date 2018/8/3 13:36
 */
public class LaplaceNoise {

    public static Boolean laplaceNoiseHandle(ArrayList<ArrayList<String>> data,
                                             ArrayList<Integer> col,
                                             double flu){
        if(col.size()==0) return true;
        if(flu<=0) return true;
        for(int i=0;i<col.size();i++){
            int colume=col.get(i);
            //寻找最大值和最小值
            double max=Double.MIN_VALUE;
            double min=Double.MAX_VALUE;
            for(int j=0;j<data.get(0).size();j++){
                try {
                    max=Double.valueOf(data.get(colume).get(j))>max? Double.valueOf(data.get(colume).get(j)):max;
                    min=Double.valueOf(data.get(colume).get(j))<min? Double.valueOf(data.get(colume).get(j)):min;
                }catch (Exception e){//直接跳过该单元格
                }
            }
            //添加噪声
            double rang=(max-min)*flu;
            String format="";
            for(int j=0;j<data.get(0).size();j++){
                try{
                    if(data.get(colume).get(j).split("\\.").length==1) format="%.0f";
                    else format="%."+data.get(colume).get(j).split("\\.")[1].length()+"f";
                    Double value=Double.valueOf(data.get(colume).get(j));
                    value=value+noiseBuilder()*rang;
                    value=value<min?min:value;
                    value=value>max?max:value;
                    data.get(colume).set(j,String.format(format,value));
                }catch (Exception e){//直接跳过该单元格
                }
            }
        }
        return true;
    }

    /**
     * 噪声生成器，范围[-1,1],-0.5和0.5为最高点
     * @return
     */
    private static double noiseBuilder(){
        RandomEngine generator;
        generator = new cern.jet.random.engine.MersenneTwister(new java.util.Date());
        Double laplace= Distributions.nextLaplace(generator);
        laplace=laplace >5 ? 5:laplace;
        laplace=laplace <-5 ? -5:laplace;
        laplace=(laplace+5)/10;
        int sign=Math.random()<0.5?1:-1;
        return laplace*sign;
    }

    public static void main(String[] args){
        ArrayList<ArrayList<String>> data=new ArrayList<ArrayList<String>> ();
        ArrayList<String> s=new ArrayList<String>();
        for(int i =0;i<100;i++)
            s.add(String.valueOf(i));
        s.add("aaa");
        data.add(s);
        ArrayList<Integer> col=new ArrayList<Integer> ();
        col.add(0);
        laplaceNoiseHandle(data,col,0.05);
        for(int i=0;i<101;i++)
            System.out.print(data.get(0).get(i)+" ");
    }
}
