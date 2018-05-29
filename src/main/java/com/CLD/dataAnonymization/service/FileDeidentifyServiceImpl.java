package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.model.FieldInfo;
import com.CLD.dataAnonymization.util.deidentifier.FileResolve;
import com.CLD.dataAnonymization.util.deidentifier.IOAdapter;
import com.CLD.dataAnonymization.util.deidentifier.ZipCompressor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 该类用于处理文件类型数据
 * @Author CLD
 * @Date 2018/4/16 8:43
 **/
@Service
public class FileDeidentifyServiceImpl implements FileDeidentifyService {

    @Autowired
    FieldClassifyService fieldClassifyService;

    @Override
    public String FileDeidentify(MultipartHttpServletRequest re, HttpServletRequest rq, String level) throws Exception {
        return FileDeidentify(re,rq,level,"Original");
    }

    @Override
    public String FileDeidentify(MultipartHttpServletRequest re, HttpServletRequest rq, String level,String fieldFromName) throws Exception {

        List<FieldInfo> fieldInfoList=fieldClassifyService.getFieldByFromName(fieldFromName);
        ArrayList<ArrayList<String>> fieldList=new ArrayList<ArrayList<String>>();
        for(FieldInfo fieldInfo:fieldInfoList){
            ArrayList<String> field=new ArrayList<String>();
            field.add(fieldInfo.getFieldName());
            field.add(fieldInfo.getFieldType());
            fieldList.add(field);
        }
        String time=String.valueOf(System.currentTimeMillis());
        String savePath =rq.getSession().getServletContext().getRealPath("/identityFiles/"+time);
        FileResolve.createFile(savePath);

        MultiValueMap<String, MultipartFile> files=re.getMultiFileMap();
        for(String key:files.keySet()) {
            MultipartFile file = files.getFirst(key);
            String filename = file.getOriginalFilename();
            if(filename.endsWith(".csv")){
                ArrayList<ArrayList<String>> data=new ArrayList<ArrayList<String>>();
                data= FileResolve.readerCsv(file.getInputStream());
                if(level.toLowerCase().equals("s"))
                    data= IOAdapter.ToSafeHarbor(data,fieldList);
                if(level.toLowerCase().equals("l"))
                    data= IOAdapter.ToLimitedSet(data,fieldList);
                FileResolve.writerCsv(savePath+"\\"+level+filename,data);
            }
            if(filename.endsWith(".xls")){
                ArrayList<ArrayList<ArrayList<String>>> data=new ArrayList<ArrayList<ArrayList<String>>>();
                data=FileResolve.readerXls(file.getInputStream());
                data=clearData(data);
                if(level.toLowerCase().equals("s"))
                    for (int i = 0; i <data.size()-1 ; i++)
                        data.set(i, IOAdapter.ToSafeHarbor(data.get(i),fieldList));
                if(level.toLowerCase().equals("l"))
                    for (int i = 0; i <data.size()-1 ; i++)
                        data.set(i, IOAdapter.ToLimitedSet(data.get(i),fieldList));
                FileResolve.writerXlsx(savePath+"\\"+level+filename+"x",data);
            }
            if(filename.endsWith(".xlsx")){
                ArrayList<ArrayList<ArrayList<String>>> data=new ArrayList<ArrayList<ArrayList<String>>>();
                data=FileResolve.readerXlsx(file.getInputStream());
                data=clearData(data);
                if(level.toLowerCase().equals("s"))
                    for (int i = 0; i <data.size()-1 ; i++)
                        data.set(i, IOAdapter.ToSafeHarbor(data.get(i),fieldList));
                if(level.toLowerCase().equals("l"))
                    for (int i = 0; i <data.size()-1 ; i++)
                        data.set(i, IOAdapter.ToLimitedSet(data.get(i),fieldList));
                FileResolve.writerXlsx(savePath+"\\"+level+filename,data);
            }
        }

        ZipCompressor zc = new ZipCompressor(savePath+".zip");
        zc.compress(savePath);
        FileResolve.deleteFile(savePath);
        //延时300秒清空zip 文件
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    FileResolve.deleteFile(savePath+".zip");
                } catch (UnsupportedEncodingException e) {e.printStackTrace();}
            }
        };
        Timer timer = new Timer();
        long delay = 300*1000;
        timer.schedule(task, delay);

        return "/identityFiles/"+time+".zip";
    }

    /**
     * 对数据进行整理，补填空缺，去除空行
     * @param data
     * @return
     */
    private  ArrayList<ArrayList<ArrayList<String>>> clearData(ArrayList<ArrayList<ArrayList<String>>> data){
        for(int i=0;i<data.size()-1;i++){
            for(int j=0;j<data.get(i).size();j++){
                boolean isEnpty=true;
                for(int k=0;k<data.get(i).get(0).size();k++){
                    if(data.get(i).get(j).size()<=k) data.get(i).get(j).add("");
                    if(data.get(i).get(j).get(k)!="") isEnpty=false;
                }
                if(isEnpty) {//去除空行
                    data.get(i).remove(j);
                    j--;
                }
            }
        }
        return data;
    }
}
