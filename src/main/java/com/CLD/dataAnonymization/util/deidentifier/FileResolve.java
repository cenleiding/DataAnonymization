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

import java.io.*;
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
    @SuppressWarnings({ "deprecation", "resource" })
    public static  ArrayList<ArrayList<ArrayList<String>>> readerXls(String xlsFilepath) throws Exception{
        ArrayList<ArrayList<ArrayList<String>>> worklist=new ArrayList<ArrayList<ArrayList<String>>>();
        ArrayList<ArrayList<String>> sheetname=new ArrayList<ArrayList<String>>();
        ArrayList<String> name=new ArrayList<String>();
        FileInputStream is=new FileInputStream(xlsFilepath);
        HSSFWorkbook workbook=new HSSFWorkbook(is);
        int sheetnum=workbook.getNumberOfSheets();
        for(int i=0;i<sheetnum;i++){
            HSSFSheet sheet=workbook.getSheetAt(i);
            name.add(sheet.getSheetName());
            ArrayList<ArrayList<String>> sheetList=new ArrayList<ArrayList<String>>();
            for (int rowNum=0;rowNum<=sheet.getLastRowNum();rowNum++){
                HSSFRow row=sheet.getRow(rowNum);
                if(row!=null){
                    int mincell=row.getFirstCellNum();
                    int maxcell=row.getLastCellNum();
                    ArrayList<String> rowList=new ArrayList<String>();
                    for (int cellNum=mincell;cellNum<maxcell;cellNum++){
                        HSSFCell cell=row.getCell(cellNum);
                        String value = "";
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_STRING:
                                    value = cell.getStringCellValue();
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    if (DateUtil.isCellDateFormatted(cell)) {
                                        Date date = cell.getDateCellValue();
                                        if (date != null) {
                                            value = new SimpleDateFormat("yyyy-MM-dd")
                                                    .format(date);
                                        } else {
                                            value = "";
                                        }
                                    } else {
                                        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
                                        nf.setGroupingUsed(false);
                                        value =nf.format(cell.getNumericCellValue());
                                    }
                                    break;
                                case Cell.CELL_TYPE_FORMULA:
                                    value = "";
                                    break;
                                case Cell.CELL_TYPE_BLANK:
                                    break;
                                case Cell.CELL_TYPE_ERROR:
                                    value = "";
                                    break;
                                case Cell.CELL_TYPE_BOOLEAN:
                                    value = (cell.getBooleanCellValue() == true ? "Y":"N");
                                    break;
                                default:
                                    value = "";
                            }
                        }
                        rowList.add(value);
                    }
                    sheetList.add(rowList);
                }
            }
            worklist.add(sheetList);
        }
        sheetname.add(name);
        worklist.add(sheetname);
        is.close();
        return worklist;
    }

    /**
     * XLSX输入,三维表单输出
     *
     * @param xlsxFilepath
     * @return
     * @throws Exception
     * @throws Exception
     */
    @SuppressWarnings({ "resource", "deprecation" })
    public static ArrayList<ArrayList<ArrayList<String>>> readerXlsx(String xlsxFilepath) throws Exception {
        ArrayList<ArrayList<ArrayList<String>>> worklist=new ArrayList<ArrayList<ArrayList<String>>>();
        ArrayList<ArrayList<String>> sheetname=new ArrayList<ArrayList<String>>();
        ArrayList<String> name=new ArrayList<String>();
        InputStream is=null;
        if(xlsxFilepath.equals("Attributes.xlsx")||xlsxFilepath.equals("Address.xlsx"))
            is=FileResolve.class.getResourceAsStream(xlsxFilepath);
        else
            is=new FileInputStream(xlsxFilepath);
        XSSFWorkbook workbook=new XSSFWorkbook(is);
        int sheetnum=workbook.getNumberOfSheets();
        for(int i=0;i<sheetnum;i++){
            XSSFSheet sheet=workbook.getSheetAt(i);
            name.add(sheet.getSheetName());
            ArrayList<ArrayList<String>> sheetList=new ArrayList<ArrayList<String>>();
            for (int rowNum=0;rowNum<=sheet.getLastRowNum();rowNum++){
                XSSFRow row=sheet.getRow(rowNum);
                if(row!=null){
                    int mincell=row.getFirstCellNum();
                    int maxcell=row.getLastCellNum();
                    ArrayList<String> rowList=new ArrayList<String>();
                    for (int cellNum=mincell;cellNum<maxcell;cellNum++){
                        XSSFCell cell=row.getCell(cellNum);
                        String value = "";
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_STRING:
                                    value = cell.getStringCellValue();
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    if (DateUtil.isCellDateFormatted(cell)) {
                                        Date date = cell.getDateCellValue();
                                        if (date != null) {
                                            value = new SimpleDateFormat("yyyy-MM-dd")
                                                    .format(date);
                                        } else {
                                            value = "";
                                        }
                                    } else {
                                        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
                                        nf.setGroupingUsed(false);
                                        value =nf.format(cell.getNumericCellValue());
                                    }
                                    break;
                                case Cell.CELL_TYPE_FORMULA:
                                    value = "";
                                    break;
                                case Cell.CELL_TYPE_BLANK:
                                    break;
                                case Cell.CELL_TYPE_ERROR:
                                    value = "";
                                    break;
                                case Cell.CELL_TYPE_BOOLEAN:
                                    value = (cell.getBooleanCellValue() == true ? "Y":"N");
                                    break;
                                default:
                                    value = "";
                            }
                        }
                        rowList.add(value);
                    }
                    sheetList.add(rowList);
                }
            }
            worklist.add(sheetList);
        }
        sheetname.add(name);
        worklist.add(sheetname);
        is.close();
        return worklist;
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
            HSSFSheet sheet = wb.createSheet(name+"(匿)");
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
            for (int i = 0; i <outdata.get(k).get(0).size(); i++)
            {
                row = sheet.createRow(i);
                for(int j=0;j<outdata.get(k).size();j++)
                {
                    row.createCell(j).setCellValue(outdata.get(k).get(j).get(i));
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
    public static boolean writerXlsx(String path,ArrayList<ArrayList<ArrayList<String>>> outdata) throws IOException{
        boolean isSuccess=true;
        XSSFWorkbook wb = new XSSFWorkbook();
        for(int k=0;k<outdata.size()-1;k++){
            String name=outdata.get(outdata.size()-1).get(0).get(k);
            XSSFSheet sheet = wb.createSheet(name+"(匿)");
            XSSFRow row = sheet.createRow(0);
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
            for (int i = 0; i <outdata.get(k).get(0).size(); i++)
            {
                row = sheet.createRow(i);
                for(int j=0;j<outdata.get(k).size();j++)
                {
                    row.createCell(j).setCellValue(outdata.get(k).get(j).get(i));
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
     * 读取字段映射表
     * @param path
     * @return JSONArray
     */
    public static JSONArray readFormMapping(String path) throws FileNotFoundException {
        JSONArray outJson=new JSONArray();
        JSONReader reader=new JSONReader(new FileReader(path));
        reader.startArray();
        while(reader.hasNext()) {
            JSONObject ja= (JSONObject) reader.readObject();
            outJson.add(ja);
        }
        reader.endArray();
        reader.close();
        return outJson;
    }

    /**
     * 读取地理信息表
     * @param path
     * @return JSONObject
     */
    public static JSONObject readAddress(String path) throws FileNotFoundException {
        JSONObject outJson=new JSONObject();
        JSONReader reader=new JSONReader(new FileReader(path));
        outJson= (JSONObject) reader.readObject();
        reader.close();
        return outJson;
    }


}
