package com.CLD.dataAnonymization.util.deidentifier.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * @description:用于CSV文件的导出
 * @Author CLD
 * @Date 2018/8/2 15:45
 */
public class CsvWriter {

    /**
     *  二维链表输入,CSV输出
     * @param csvFilePath,list
     * @param list
     */
    public static boolean writerCsv(String csvFilePath, ArrayList<ArrayList<String>> list) {
        boolean isSucess=false;
        com.csvreader.CsvWriter writer = null;
        try {
            writer = new com.csvreader.CsvWriter(csvFilePath, ',', Charset.forName("utf-8"));
            for (int i=0;i<list.size();i++){
                String[] d=new String[list.get(i).size()];
                for(int j=0;j<list.get(i).size();j++)
                    d[j]=list.get(i).get(j);
                writer.writeRecord(d);
            }
            isSucess=true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        return isSucess;
    }
}
