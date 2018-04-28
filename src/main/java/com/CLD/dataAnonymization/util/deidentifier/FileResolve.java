package com.CLD.dataAnonymization.util.deidentifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 该类用于文件的解析，读取，存储。
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
    public static ArrayList<ArrayList<String>> readerCsv(String csvFilePath) throws Exception {
        ArrayList<String[]> dataList=new ArrayList<String[]>();
        ArrayList<ArrayList<String>> outlist=new ArrayList<ArrayList<String>>();
        CsvReader reader = new CsvReader(csvFilePath, ',', Charset.forName("utf-8"));
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

    /**
     * CSV读入,二维表单输出
     *
     * @param inputStream
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static ArrayList<ArrayList<String>> readerCsv(InputStream inputStream) throws Exception {
        ArrayList<String[]> dataList=new ArrayList<String[]>();
        ArrayList<ArrayList<String>> outlist=new ArrayList<ArrayList<String>>();
        CsvReader reader = new CsvReader(inputStream, ',', Charset.forName("utf-8"));
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

    /**
     *  二维表单输入,CSV输出
     *
     * @param csvFilePath,list
     * @param list
     */
    public static boolean writerCsv(String csvFilePath, ArrayList<ArrayList<String>> list) {
        boolean isSucess=false;
        CsvWriter writer = null;
        try {
            writer = new CsvWriter(csvFilePath, ',', Charset.forName("utf-8"));
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

    /**
     *  XLS读入,三维表单输出
     *
     * @param xlsFilepath
     * @return
     * @throws Exception
     */
//    @SuppressWarnings({ "deprecation", "resource" })
//    public static  ArrayList<ArrayList<ArrayList<String>>> readerXls(String xlsFilepath) throws Exception{
//        return readerXls(new FileInputStream(xlsFilepath));
//    }
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
//    @SuppressWarnings({ "deprecation", "resource" })
//    public static  ArrayList<ArrayList<ArrayList<String>>> readerXls(InputStream inputStream) throws Exception{
//        ArrayList<ArrayList<ArrayList<String>>> worklist=new ArrayList<ArrayList<ArrayList<String>>>();
//        ArrayList<ArrayList<String>> sheetname=new ArrayList<ArrayList<String>>();
//        ArrayList<String> name=new ArrayList<String>();
//        HSSFWorkbook workbook=new HSSFWorkbook(inputStream);
//        int sheetnum=workbook.getNumberOfSheets();
//        for(int i=0;i<sheetnum;i++){
//            HSSFSheet sheet=workbook.getSheetAt(i);
//            name.add(sheet.getSheetName());
//            ArrayList<ArrayList<String>> sheetList=new ArrayList<ArrayList<String>>();
//            for (int rowNum=0;rowNum<=sheet.getLastRowNum();rowNum++){
//                HSSFRow row=sheet.getRow(rowNum);
//                if(row!=null){
//                    int mincell=row.getFirstCellNum();
//                    int maxcell=row.getLastCellNum();
//                    ArrayList<String> rowList=new ArrayList<String>();
//                    for (int cellNum=mincell;cellNum<maxcell;cellNum++){
//                        HSSFCell cell=row.getCell(cellNum);
//                        String value = "";
//                        if (cell != null) {
//                            switch (cell.getCellType()) {
//                                case Cell.CELL_TYPE_STRING:
//                                    value = cell.getStringCellValue();
//                                    break;
//                                case Cell.CELL_TYPE_NUMERIC:
//                                    if (DateUtil.isCellDateFormatted(cell)) {
//                                        Date date = cell.getDateCellValue();
//                                        if (date != null) {
//                                            value = new SimpleDateFormat("yyyy-MM-dd")
//                                                    .format(date);
//                                        } else {
//                                            value = "";
//                                        }
//                                    } else {
//                                        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
//                                        nf.setGroupingUsed(false);
//                                        value =nf.format(cell.getNumericCellValue());
//                                    }
//                                    break;
//                                case Cell.CELL_TYPE_FORMULA:
//                                    value = "";
//                                    break;
//                                case Cell.CELL_TYPE_BLANK:
//                                    break;
//                                case Cell.CELL_TYPE_ERROR:
//                                    value = "";
//                                    break;
//                                case Cell.CELL_TYPE_BOOLEAN:
//                                    value = (cell.getBooleanCellValue() == true ? "Y":"N");
//                                    break;
//                                default:
//                                    value = "";
//                            }
//                        }
//                        rowList.add(value);
//                    }
//                    sheetList.add(rowList);
//                }
//            }
//            worklist.add(sheetList);
//        }
//        sheetname.add(name);
//        worklist.add(sheetname);
//        inputStream.close();
//        return worklist;
//    }
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
//    @SuppressWarnings({ "deprecation", "resource" })
//    public static  ArrayList<ArrayList<ArrayList<String>>> readerXlsx(String xlsFilepath) throws Exception{
//        return readerXlsx(new FileInputStream(xlsFilepath));
//    }
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
//    @SuppressWarnings({ "resource", "deprecation" })
//    public static ArrayList<ArrayList<ArrayList<String>>> readerXlsx(InputStream inputStream) throws Exception {
//        ArrayList<ArrayList<ArrayList<String>>> worklist=new ArrayList<ArrayList<ArrayList<String>>>();
//        ArrayList<ArrayList<String>> sheetname=new ArrayList<ArrayList<String>>();
//        ArrayList<String> name=new ArrayList<String>();
//        XSSFWorkbook workbook=new XSSFWorkbook(inputStream);
//        int sheetnum=workbook.getNumberOfSheets();
//        for(int i=0;i<sheetnum;i++){
//            XSSFSheet sheet=workbook.getSheetAt(i);
//            name.add(sheet.getSheetName());
//            ArrayList<ArrayList<String>> sheetList=new ArrayList<ArrayList<String>>();
//            for (int rowNum=0;rowNum<=sheet.getLastRowNum();rowNum++){
//                XSSFRow row=sheet.getRow(rowNum);
//                if(row!=null){
//                    int mincell=row.getFirstCellNum();
//                    int maxcell=row.getLastCellNum();
//                    ArrayList<String> rowList=new ArrayList<String>();
//                    for (int cellNum=mincell;cellNum<maxcell;cellNum++){
//                        XSSFCell cell=row.getCell(cellNum);
//                        String value = "";
//                        if (cell != null) {
//                            switch (cell.getCellType()) {
//                                case Cell.CELL_TYPE_STRING:
//                                    value = cell.getStringCellValue();
//                                    break;
//                                case Cell.CELL_TYPE_NUMERIC:
//                                    if (DateUtil.isCellDateFormatted(cell)) {
//                                        Date date = cell.getDateCellValue();
//                                        if (date != null) {
//                                            value = new SimpleDateFormat("yyyy-MM-dd")
//                                                    .format(date);
//                                        } else {
//                                            value = "";
//                                        }
//                                    } else {
//                                        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
//                                        nf.setGroupingUsed(false);
//                                        value =nf.format(cell.getNumericCellValue());
//                                    }
//                                    break;
//                                case Cell.CELL_TYPE_FORMULA:
//                                    value = "";
//                                    break;
//                                case Cell.CELL_TYPE_BLANK:
//                                    break;
//                                case Cell.CELL_TYPE_ERROR:
//                                    value = "";
//                                    break;
//                                case Cell.CELL_TYPE_BOOLEAN:
//                                    value = (cell.getBooleanCellValue() == true ? "Y":"N");
//                                    break;
//                                default:
//                                    value = "";
//                            }
//                        }
//                        rowList.add(value);
//                    }
//                    sheetList.add(rowList);
//                }
//            }
//            worklist.add(sheetList);
//        }
//        sheetname.add(name);
//        worklist.add(sheetname);
//        inputStream.close();
//        return worklist;
//    }

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
        boolean isSuccess=true;
        HSSFWorkbook wb = new HSSFWorkbook();
        for(int k=0;k<outdata.size()-1;k++){
            String name=outdata.get(outdata.size()-1).get(0).get(k);
            HSSFSheet sheet = wb.createSheet(name);
            HSSFRow row = sheet.createRow(0);
            if(outdata.get(k).isEmpty()){//处理空页
                try {
                    FileOutputStream fout=new FileOutputStream(path);
                    wb.write(fout);
                    fout.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    isSuccess=false;
                }
                continue;
            }
            for (int i = 0; i <outdata.get(k).size(); i++)
            {
                row = sheet.createRow(i);
                for(int j=0;j<outdata.get(k).get(0).size();j++)
                {
                    row.createCell(j).setCellValue(outdata.get(k).get(i).get(j));
                }
                try {
                    FileOutputStream fout=new FileOutputStream(path);
                    wb.write(fout);
                    fout.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    isSuccess=false;
                }
            }
        }
        wb.close();
        return isSuccess;
    }

    /**
     * 三维数据输入,XlSX导出
     *
     * @param path
     * @param outdata
     * @return
     * @throws IOException
     */
//    public static boolean writerXlsx(String path,ArrayList<ArrayList<ArrayList<String>>> outdata) throws IOException{
//        boolean isSuccess=true;
//        XSSFWorkbook wb = new XSSFWorkbook();
//        for(int k=0;k<outdata.size()-1;k++){
//            String name=outdata.get(outdata.size()-1).get(0).get(k);
//            XSSFSheet sheet = wb.createSheet(name);
//            XSSFRow row = sheet.createRow(0);
//            if(outdata.get(k).isEmpty()){//处理空页
//                try {
//                    FileOutputStream fout=new FileOutputStream(path);
//                    wb.write(fout);
//                    fout.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    isSuccess=false;
//                }
//                continue;
//            }
//            for (int i = 0; i <outdata.get(k).size(); i++)
//            {
//                row = sheet.createRow(i);
//                for(int j=0;j<outdata.get(k).get(0).size();j++)
//                {
//                    row.createCell(j).setCellValue(outdata.get(k).get(i).get(j));
//                }
//                try {
//                    FileOutputStream fout=new FileOutputStream(path);
//                    wb.write(fout);
//                    fout.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    isSuccess=false;
//                }
//            }
//        }
//        wb.close();
//        return isSuccess;
//    }
    public static boolean writerXlsx(String path,ArrayList<ArrayList<ArrayList<String>>> outdata) throws IOException{
        XlsxStreamingWriter xlsxStreamingWriter=new XlsxStreamingWriter(outdata,path);
        return xlsxStreamingWriter.writer();
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
