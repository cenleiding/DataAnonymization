package com.CLD.dataAnonymization.util.deidentifier;

import com.CLD.dataAnonymization.util.deidentifier.io.FileResolve;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/8 10:37
 */
public class AnonymizerTest {
    @Test
    public void anonymize() throws Exception {
        ArrayList<ArrayList<String>> data= FileResolve.readerXlsx("C:\\Users\\CLD\\Desktop\\test.xlsx").get(0);
        DataHandle dataHandle=new DataHandle(data);
        Configuration configuration=new Configuration();
        configuration.setLevel(Configuration.AnonymousLevel.Level2);
        Anonymizer anonymizer=new Anonymizer(dataHandle,configuration);
        anonymizer.anonymize();

        ArrayList<ArrayList<ArrayList<String>>> workbook=new ArrayList<ArrayList<ArrayList<String>>>();
        ArrayList<ArrayList<String>> names=new ArrayList<ArrayList<String>>();
        ArrayList<String> sheetName=new ArrayList<String>();
        sheetName.add("text2");
        names.add(sheetName);
        workbook.add(dataHandle.getData());
        workbook.add(names);
        FileResolve.writerXlsx("C:\\Users\\CLD\\Desktop\\test2.xlsx",workbook);


    }

}