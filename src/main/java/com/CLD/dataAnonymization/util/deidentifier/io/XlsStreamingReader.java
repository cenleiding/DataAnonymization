package com.CLD.dataAnonymization.util.deidentifier.io;

import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * 该类以流事件的形式读取大型XLSX文件
 * 所有数据将以字符串格式获取，原时间格式数据将会以时间戳形式展示
 * 参考https://blog.csdn.net/lipinganq/article/details/77678443
 * @Author CLD
 * @Date 2018/4/23 7:29
 **/
public class XlsStreamingReader{

    private ArrayList<ArrayList<ArrayList<String>>> workbook=new ArrayList<ArrayList<ArrayList<String>>>();
    private ArrayList<ArrayList<String>> sheet=new ArrayList<ArrayList<String>>();
    private ArrayList<String> row=new ArrayList<String>();
    private ArrayList<String> sheetName=new ArrayList<String>();

    public XlsStreamingReader(String filePath) throws IOException {
        FileInputStream is = new FileInputStream(filePath);
        process(is);
    }

    public XlsStreamingReader(InputStream inputStream) throws IOException {
        process(inputStream);
    }

    public void process(InputStream inputStream) throws IOException {
        POIFSFileSystem poifs = null;
        InputStream din = null;
        try {
            poifs = new POIFSFileSystem(inputStream);
            din = poifs.createDocumentInputStream("Workbook");
            HSSFRequest req = new HSSFRequest();
            // 为HSSFRequest增加listener
            req.addListenerForAllRecords(new XlsListener());
            HSSFEventFactory factory = new HSSFEventFactory();
            // 处理inputstream
            factory.processEvents(req, din);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // 关闭inputstream
            inputStream.close();
            din.close();
        }


    }


    public ArrayList<ArrayList<ArrayList<String>>> getData(){
        return workbook;
    }

    //继承HSSFListener
    private class XlsListener  implements HSSFListener
    {
        private SSTRecord sstrec;
        private int rowindex=0;
        private int sheetnum=0;
        private int sheetindex=-1;

        /**
         * 该方法用于添加空格
         * @param l 已存列数
         * @param r 当前列数
         */
        public void addBlank(int l,int r){
            for(int i=0;i<r-l;i++)
                row.add("");
        }
        /**
         * 实现接口方法用于处理每一条记录，包括workbook/row/cell
         */
        @Override
        public void processRecord(Record record)
        {
            switch (record.getSid())
            {
                // 标记workbook或sheet开始
                case BOFRecord.sid:
                    BOFRecord bof = (BOFRecord) record;
                    if (bof.getType() == bof.TYPE_WORKBOOK)
                    {
                        System.out.println("Encountered workbook");
                    } else if (bof.getType() == bof.TYPE_WORKSHEET)
                    {
                        rowindex=0;
                        sheetindex++;
                    }
                    break;
                //标记workbook或sheet结束
                case EOFRecord.sid:
                    EOFRecord erec=(EOFRecord) record;
                    if(sheetindex!=-1){//存储表
                        sheet.add((ArrayList<String>) row.clone());
                        workbook.add((ArrayList<ArrayList<String>>) sheet.clone());
                        sheet.clear();
                        row.clear();
                    }
                    if(sheetindex==(sheetnum-1)){//存储表名
                        sheet.add((ArrayList<String>) sheetName.clone());
                        workbook.add((ArrayList<ArrayList<String>>) sheet.clone());
                        sheet.clear();
                        sheetName.clear();
                    }
                    break;

                //处理sheet
                case BoundSheetRecord.sid:
                    BoundSheetRecord bsr = (BoundSheetRecord) record;
                    sheetName.add(bsr.getSheetname());
                    sheetnum++;
                    break;
                //处理行,如行索引，行是否隐藏
                case RowRecord.sid:
                    break;
                //处理列,如列是否隐藏
                case ColumnInfoRecord.sid:
                    break;
                // 包含sheet中所有文本单元格
                case SSTRecord.sid:
                    sstrec = (SSTRecord) record;
                    break;
                //处理数字单元格:数字单元格和日期单元格
                case NumberRecord.sid:
                    NumberRecord numrec = (NumberRecord) record;
                    if(rowindex<numrec.getRow()){
                        sheet.add((ArrayList<String>) row.clone());
                        row.clear();
                        rowindex++;
                    }
                    addBlank(row.size(),numrec.getColumn());
                    row.add(String.valueOf(numrec.getValue())
                            .replaceAll("0+?$","")
                            .replaceAll("[.]$",""));
                    break;
                //处理文本单元格
                case LabelSSTRecord.sid:
                    LabelSSTRecord lrec = (LabelSSTRecord) record;
                    if(rowindex<lrec.getRow()){
                        sheet.add((ArrayList<String>) row.clone());
                        row.clear();
                        rowindex++;
                    }
                    addBlank(row.size(),lrec.getColumn());
                    row.add(String.valueOf(String.valueOf(sstrec.getString(lrec.getSSTIndex()))));
                    break;
                //布尔或错误单元格
                case BoolErrRecord.sid:
                    BoolErrRecord ber=(BoolErrRecord) record;
                    if(ber.isError()){
                        if(rowindex<ber.getRow()){
                            sheet.add((ArrayList<String>) row.clone());
                            row.clear();
                            rowindex++;
                        }
                        addBlank(row.size(),ber.getColumn());
                        row.add("");

                    }
                    System.out.println("错误");
                    if(ber.isBoolean()){
                        if(rowindex<ber.getRow()){
                            sheet.add((ArrayList<String>) row.clone());
                            row.clear();
                            rowindex++;
                        }
                        addBlank(row.size(),ber.getColumn());
                        row.add(String.valueOf(ber.getBooleanValue()));
                    }
                    break;
                //空单元格
                case BlankRecord.sid:
                    BlankRecord brec=(BlankRecord) record;
                    if(rowindex<brec.getRow()){
                        sheet.add((ArrayList<String>) row.clone());
                        row.clear();
                        rowindex++;
                    }
                    addBlank(row.size(),brec.getColumn());
                    row.add("");
                    break;

            }
        }

    }

//    public static void main(String[] args) throws Exception
//    {
//       XlsStreamingReader xlsStreamingReader=new XlsStreamingReader("C:\\Users\\CLD\\Desktop\\1.xls");
//        ArrayList<ArrayList<ArrayList<String>>> w = xlsStreamingReader.getData();
//    }
}