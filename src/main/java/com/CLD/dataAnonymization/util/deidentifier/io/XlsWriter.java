package com.CLD.dataAnonymization.util.deidentifier.io;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @description:用于输出Xls文件
 * @Author CLD
 * @Date 2018/8/2 15:56
 */
public class XlsWriter {

    /**
     * 三维表单输入,XLS导出
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
}
