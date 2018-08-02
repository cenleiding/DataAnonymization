package com.CLD.dataAnonymization.util.deidentifier.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * @description: 用于csv文件的读取,以二维链表形式返回
 * @Author CLD
 * @Date 2018/8/2 15:33
 */
public class CsvReader {

    /**
     * CSV读入,二维表单输出
     * @param csvFilePath
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static ArrayList<ArrayList<String>> readerCsv(String csvFilePath) throws IOException {
        com.csvreader.CsvReader reader = new com.csvreader.CsvReader(csvFilePath, ',', Charset.forName("utf-8"));
        return reader(reader);
    }

    /**
     * CSV读入,二维表单输出
     * @param inputStream
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static ArrayList<ArrayList<String>> readerCsv(InputStream inputStream) throws IOException {
        com.csvreader.CsvReader reader = new com.csvreader.CsvReader(inputStream, ',', Charset.forName("utf-8"));
        return reader(reader);
    }

    /**
     * CSV读入,二维表单输出
     * @param reader
     * @return
     * @throws IOException
     */
    public static ArrayList<ArrayList<String>> reader(com.csvreader.CsvReader reader) throws IOException {
        ArrayList<String[]> dataList=new ArrayList<String[]>();
        ArrayList<ArrayList<String>> outlist=new ArrayList<ArrayList<String>>();
        reader.readHeaders();
        dataList.add(reader.getHeaders());
        while (reader.readRecord()) {
            dataList.add(reader.getValues());
        }
        for (int i = 0; i < dataList.size(); i++) {
            ArrayList<String> list=new ArrayList<String>();
            for(int j=0;j<dataList.get(i).length;j++)
                list.add(dataList.get(i)[j]);
            outlist.add(list);
        }
        reader.close();
        return outlist;
    }
}
