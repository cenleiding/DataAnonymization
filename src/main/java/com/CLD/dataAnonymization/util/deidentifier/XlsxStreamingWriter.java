package com.CLD.dataAnonymization.util.deidentifier;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * 该类用SXSSF实现流式的创建大型xlsx文件
 * @Author CLD
 * @Date 2018/4/23 16:08
 **/
public class XlsxStreamingWriter {

    private ArrayList<ArrayList<ArrayList<String>>> outdata=null;

    private String path;

    public XlsxStreamingWriter(ArrayList<ArrayList<ArrayList<String>>> outdata,String path){
       this.outdata=outdata;
       this.path=path;
    }

    public Boolean writer(){
        SXSSFWorkbook wb =  null;
        boolean isSuccess=true;
        try {
            System.out.println("xlsx文件导出："+path);
            wb=new SXSSFWorkbook();
            for(int k=0;k<outdata.size()-1;k++){
                String name=outdata.get(outdata.size()-1).get(0).get(k);
                Sheet sheet = wb.createSheet(name);
                if(outdata.get(k).isEmpty()){//处理空页
                    continue;
                }
                for (int i = 0; i <outdata.get(k).size(); i++)
                {
                    Row row = sheet.createRow(i);
                    for(int j=0;j<outdata.get(k).get(i).size();j++)
                    {
                        row.createCell(j).setCellValue(outdata.get(k).get(i).get(j));
                    }
                }
            }
            FileOutputStream fout=new FileOutputStream(path);
            wb.write(fout);
            fout.close();
            System.out.println("xlsx文件导出成功！");
        }catch (Exception ex){
            ex.printStackTrace();
            isSuccess=false;
            System.out.println("xlsx文件导出失败！");
        } finally {
            // 删除临时文件
            if(wb!=null){
                wb.dispose();
            }
        }
        return isSuccess;
    }

//    public static void main(String[] args) throws Exception {
//        XlsxStreamingReader xlsStreamingReader=new XlsxStreamingReader("C:\\Users\\CLD\\Desktop\\test.xlsx");
//        ArrayList<ArrayList<ArrayList<String>>> w = xlsStreamingReader.getData();
//        XlsxStreamingWriter xlsxStreamingWriter=new XlsxStreamingWriter(w,"C:\\Users\\CLD\\Desktop\\testbig.xlsx");
//        xlsxStreamingWriter.writer();
//    }
}
