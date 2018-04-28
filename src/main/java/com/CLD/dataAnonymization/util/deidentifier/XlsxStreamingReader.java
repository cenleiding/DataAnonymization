package com.CLD.dataAnonymization.util.deidentifier;


import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 该类以流事件的形式读取大型XLSX文件
 * 所有数据将以字符串格式获取，原时间格式数据将会以时间戳形式展示
 * @Author CLD
 * @Date 2018/4/19 21:11
 **/
public class XlsxStreamingReader {

    private ArrayList<ArrayList<ArrayList<String>>> workbook=new ArrayList<ArrayList<ArrayList<String>>>();
    private ArrayList<ArrayList<String>> sheet=new ArrayList<ArrayList<String>>();
    private ArrayList<String> row=new ArrayList<String>();

    public XlsxStreamingReader(String filePath) throws Exception {
        OPCPackage pkg = OPCPackage.open(filePath);
        process(pkg);
    }
    public XlsxStreamingReader(InputStream inputStream) throws Exception {
        OPCPackage pkg = OPCPackage.open(inputStream);
        process(pkg);
    }

    public ArrayList<ArrayList<ArrayList<String>>> getData(){
        return workbook;
    }

    private void process(OPCPackage pkg) throws Exception {
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);

        System.out.println("xlsx文件开始解析");
        // 遍历sheet
        Iterator<InputStream> sheets=r.getSheetsData();
        while(sheets.hasNext()){
            InputStream s=sheets.next();
            InputSource sheetSource = new InputSource(s);
            parser.parse(sheetSource);
            s.close();
            workbook.add((ArrayList<ArrayList<String>>) sheet.clone());
        }

        //获取sheetName
        InputStream s=r.getWorkbookData();
        InputSource sheetSource = new InputSource(s);
        parser.parse(sheetSource);
        s.close();
        workbook.add((ArrayList<ArrayList<String>>) sheet.clone());

        System.out.println("xlsx文件解析完毕");

    }



    private XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser =
                XMLReaderFactory.createXMLReader(
                        "org.apache.xerces.parsers.SAXParser"
                );
        ContentHandler handler = new SheetHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }

    /**
     * 处理sax的handler
     */
    private class SheetHandler extends DefaultHandler{
        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;


        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
        }

        @Override
        public void startDocument(){
            sheet.clear();
            row.clear();
        }

        @Override
        public void endDocument()throws SAXException{
            sheet.add((ArrayList<String>) row.clone());
        }

        //元素开始时的handler
        @Override
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            // sheet => 表单信息
            if(name.equals("sheet")){
                row.add(attributes.getValue("name"));
            }
            //row => 行开始
            if(name.equals("row")){
                if(!row.isEmpty())
                sheet.add((ArrayList<String>) row.clone());
                row.clear();
            }
            // c => 单元格
            if(name.equals("c")) {
                //用于补充空单元格
                int num=0;
                int size=row.size();
                String col=attributes.getValue("r").replaceAll("[0-9]*","");
                for(int i=col.length();i>0;i--){
                    num=num*26+(col.charAt(i-1)-'A')+1;
                }
                for (int i = 0; i<(num-size-1); i++) {
                    row.add("");
                }
                // 获取单元格类型
                String cellType = attributes.getValue("t");
                if(cellType != null && cellType.equals("s")) {
                    nextIsString = true;
                } else {
                    nextIsString = false;
                }
            }
            lastContents = "";
        }

        //元素结束时的handler
        @Override
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            if(nextIsString) {
                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                nextIsString = false;
            }
            else if((lastContents!="")&&(name.equals("v"))){
                Double d=Double.valueOf(lastContents);
                lastContents=String.valueOf(d)
                        .replaceAll("0+?$","")
                        .replaceAll("[.]$","");
            }

            // v => 单元格内容
            if(name.equals("v")) {
                row.add(lastContents);
            }
        }

        //读取元素间内容时的handler
        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            lastContents += new String(ch, start, length);
        }
    }


    public static void main(String[] args) throws Exception {
        XlsxStreamingReader example = new XlsxStreamingReader("C:\\Users\\CLD\\Desktop\\test.xlsx");
        ArrayList<ArrayList<ArrayList<String>>> d=example.getData();
    }


}
