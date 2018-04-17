package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.util.deidentifier.FileResolve;
import com.CLD.dataAnonymization.util.deidentifier.IOConfiguration;
import com.CLD.dataAnonymization.util.deidentifier.ZipCompressor;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 该类用于处理文件类型数据
 * @Author CLD
 * @Date 2018/4/16 8:43
 **/
@Service
public class FileDeidentifyImpl implements FileDeidentify{

    @Override
    public String FileDeidentify(MultipartHttpServletRequest re, HttpServletRequest rq, String level) throws Exception {

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
                    data= IOConfiguration.ToSafeHarbor(data);
                if(level.toLowerCase().equals("l"))
                    data= IOConfiguration.ToLimitedSet(data);
                FileResolve.writerCsv(savePath+"\\"+level+filename,data);
            }
            if(filename.endsWith(".xls")){
                ArrayList<ArrayList<ArrayList<String>>> data=new ArrayList<ArrayList<ArrayList<String>>>();
                data=FileResolve.readerXls(file.getInputStream());
                if(level.toLowerCase().equals("s"))
                    for (int i = 0; i <data.size()-1 ; i++)
                        data.set(i, IOConfiguration.ToSafeHarbor(data.get(i)));
                if(level.toLowerCase().equals("l"))
                    for (int i = 0; i <data.size()-1 ; i++)
                        data.set(i, IOConfiguration.ToLimitedSet(data.get(i)));
                FileResolve.writerXls(savePath+"\\"+level+filename,data);
            }
            if(filename.endsWith(".xlsx")){
                ArrayList<ArrayList<ArrayList<String>>> data=new ArrayList<ArrayList<ArrayList<String>>>();
                data=FileResolve.readerXlsx(file.getInputStream());
                if(level.toLowerCase().equals("s"))
                    for (int i = 0; i <data.size()-1 ; i++)
                        data.set(i, IOConfiguration.ToSafeHarbor(data.get(i)));
                if(level.toLowerCase().equals("l"))
                    for (int i = 0; i <data.size()-1 ; i++)
                        data.set(i, IOConfiguration.ToLimitedSet(data.get(i)));
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
}
