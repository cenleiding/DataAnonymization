package com.CLD.dataAnonymization.util.deidentifier.io;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * 该类用于文件的解析，读取，存储。
 * xlx,csv,xlxs
 * @Author CLD
 * @Date 2018/4/12 15:40
 **/
public class FileResolve {

    /**
     * CSV读入,二维表单输出
     *
     * @param csvFilePath
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static ArrayList<ArrayList<String>> readerCsv(String csvFilePath) throws IOException {
        return CsvReader.readerCsv(csvFilePath);
    }

    /**
     * CSV读入,二维表单输出
     *
     * @param inputStream
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static ArrayList<ArrayList<String>> readerCsv(InputStream inputStream) throws IOException {
        return CsvReader.readerCsv(inputStream);
    }

    /**
     *  二维链表输入,CSV输出
     * @param csvFilePath,list
     * @param list
     */
    public static boolean writerCsv(String csvFilePath, ArrayList<ArrayList<String>> list) {
        return CsvWriter.writerCsv(csvFilePath,list);
    }

    /**
     *  XLS读入,三维表单输出
     * @param xlsFilepath
     * @return
     * @throws Exception
     */
      public static  ArrayList<ArrayList<ArrayList<String>>> readerXls(String xlsFilepath) throws IOException {
          XlsStreamingReader xlsStreamingReader=new XlsStreamingReader(xlsFilepath);
          return xlsStreamingReader.getData();
      }

    /**
     *  XLS读入,三维表单输出
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static  ArrayList<ArrayList<ArrayList<String>>> readerXls(InputStream inputStream) throws IOException {
        XlsStreamingReader xlsStreamingReader=new XlsStreamingReader(inputStream);
        return xlsStreamingReader.getData();
    }

    /**
     *  XLSX读入,三维表单输出
     *
     * @param xlsFilepath
     * @return
     * @throws Exception
     */
    public static  ArrayList<ArrayList<ArrayList<String>>> readerXlsx(String xlsFilepath) throws Exception{
        XlsxStreamingReader xlsxStreamingReader=new XlsxStreamingReader(xlsFilepath);
        return  xlsxStreamingReader.getData();
    }

    /**
     * XLSX输入,三维表单输出
     *
     * @param inputStream
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static ArrayList<ArrayList<ArrayList<String>>> readerXlsx(InputStream inputStream) throws Exception {
        XlsxStreamingReader xlsxStreamingReader=new XlsxStreamingReader(inputStream);
        return  xlsxStreamingReader.getData();
    }

    /**
     * 三维表单输入,XLS导出
     *
     * @param path
     * @param outdata
     * @return
     * @throws IOException
     */
    public static boolean writerXls(String path,ArrayList<ArrayList<ArrayList<String>>> outdata) throws IOException{
        return writerXls(path,outdata);
    }

    /**
     * 三维数据输入,XlSX导出
     *
     * @param path
     * @param outdata
     * @return
     * @throws IOException
     */
    public static boolean writerXlsx(String path,ArrayList<ArrayList<ArrayList<String>>> outdata) throws IOException{
        XlsxWriter xlsxWriter =new XlsxWriter(outdata,path);
        return xlsxWriter.writer();
    }


    /**  json文件读取方法组*/

    public static JSONObject readerObjectJson(String path) throws FileNotFoundException {
        return JsonReader.jsonObjectReader(path);
    }

    public static JSONObject readerObjectJson(InputStream is) throws FileNotFoundException {
        return JsonReader.jsonObjectReader(is);
    }

    public static JSONArray readerArrayJson(String path) throws FileNotFoundException {
        return JsonReader.jsonArrayReader(path);
    }

    public static JSONArray readerArrayJson(InputStream is) throws FileNotFoundException {
        return JsonReader.jsonArrayReader(is);
    }

    /**
     * 生成文件夹
     * @param path
     * @throws UnsupportedEncodingException
     */
    public static void createFile(String path) throws UnsupportedEncodingException{
        path= URLDecoder.decode(path, "utf-8");
        File tempfile = new File(path);
        if (!tempfile.exists()) {
            System.out.println(path+"目录不存在，需要创建");
            System.out.println(tempfile.mkdirs());
        }
    }

    /**
     * 存储文件
     * @param file
     * @param path
     * @throws UnsupportedEncodingException
     */
    public static void saveFile(MultipartFile file, String path) throws UnsupportedEncodingException{
        path=URLDecoder.decode(path, "utf-8");
        try {
            InputStream in = file.getInputStream();
            FileOutputStream out = new FileOutputStream(path);
            byte buffer[] = new byte[4*1024];
            int len = 0;
            while((len=in.read(buffer))>0){
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *删除文件夹
     * @param path
     */
    public static void deleteFile(String path) throws UnsupportedEncodingException{
        path=URLDecoder.decode(path, "utf-8");
        File file=new File(path);
        deleteFile(file);
    }

    private static void deleteFile(File file) {
        if (file.exists()) {//判断文件是否存在
            if (file.isFile()) {//判断是否是文件
                file.delete();//删除文件
            } else if (file.isDirectory()) {//否则如果它是一个目录
                File[] files = file.listFiles();//声明目录下所有的文件 files[];
                for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件
                    deleteFile(files[i]);//把每个文件用这个方法进行迭代
                }
                file.delete();//删除文件夹
            }
        } else {
            System.out.println("所删除的文件不存在");
        }
    }


}
