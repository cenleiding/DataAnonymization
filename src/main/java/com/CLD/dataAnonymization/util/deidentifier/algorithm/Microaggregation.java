package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import java.util.ArrayList;

/**
 * @description: 该类对敏感数值进行微聚集处理，为保证数据可用性，每列进行单独的处理。
 * 该方法与传统的微聚集处理不同，将不依附于K-匿名法，而采用按比例分类
 * @Author CLD
 * @Date 2018/8/8 8:58
 */
public class Microaggregation {

    public static Boolean microaggregationHandle(ArrayList<ArrayList<String>> data,
                                          ArrayList<Integer> col,
                                          Integer gradation){
        for(int column : col){
            int len =counting(data.get(column));
            double[] num=getNum(data.get(column),len);
            int[] pos=getPos(data.get(column),len);
            quickSort(num,pos);
            aggregate(num,gradation);
            updata(data,column,num,pos);
        }
        return true;
    }

    private static void updata(ArrayList<ArrayList<String>> data,int col,double[] num,int[] pos){
        for(int i=0;i<pos.length;i++)
            data.get(col).set(pos[i],String.valueOf(num[i]));
    }


    private static void aggregate( double[] num,int gradation){
        int n=num.length/gradation;
        int index=0;
        while (index!=num.length){
            int end= index+n>num.length? num.length:index+n;
            double value=0;
            for(int i=index;i<end;i++)
                value+=num[i];
            value=value/(end-index);
            for(int i=index;i<end;i++)
                num[i]= Double.valueOf((int)value);
            index=end;
        }
    }

    private static int counting(ArrayList<String> d){
        int len=0;
        for(int i=1;i<d.size();i++)
            if(!d.get(i).equals(""))len++;
        return len;
    }

    private static double[] getNum(ArrayList<String> d,int len){
        double[] num=new double[len];
        int p=0;
        for(int i=1;i<d.size();i++)
            if(!d.get(i).equals(""))num[p++]=Double.valueOf(d.get(i));
        return num;
    }

    private static int[] getPos(ArrayList<String> d,int len){
        int[] pos=new int[len];
        int p=0;
        for(int i=1;i<d.size();i++)
            if(!d.get(i).equals(""))pos[p++]=i;
        return pos;
    }


    private static void quickSort(double[] arr,int[] pos){
        qsort(arr,pos, 0, arr.length-1);
    }
    private static void qsort(double[] arr,int[] pos, int low, int high){
        if (low < high){
            int pivot=partition(arr,pos, low, high);
            qsort(arr,pos, low, pivot-1);
            qsort(arr,pos, pivot+1, high);
        }
    }
    private static int partition(double[] arr,int[] pos, int low, int high){
        double pivot = arr[low];
        int pipos = pos[low];
        while (low<high){
            while (low<high && arr[high]>=pivot) --high;
            arr[low]=arr[high];
            pos[low]=pos[high];
            while (low<high && arr[low]<=pivot) ++low;
            arr[high]=arr[low];
            pos[high]=pos[low];
        }
        arr[low] = pivot;
        pos[low]=pipos;
        return low;
    }
}
