package com.CLD.dataAnonymization.service.deidentifyTarget.fileDeidentify;

import com.CLD.dataAnonymization.dao.h2.entity.FieldClassifyUsageCount;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyUsageCountRepository;
import com.CLD.dataAnonymization.model.AnonymizeConfigure;
import com.CLD.dataAnonymization.service.deidentifyTarget.EasyUtil;
import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
import com.CLD.dataAnonymization.util.deidentifier.DataHandle;
import com.CLD.dataAnonymization.util.deidentifier.io.FileResolve;
import com.CLD.dataAnonymization.util.deidentifier.io.ZipCompressor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @description:该类用于处理文件类型数据
 * @Author CLD
 * @Date 2018/4/16 8:43
 **/
@Service
public class FileDeidentifyServiceImpl implements FileDeidentifyService {

    @Autowired
    FieldClassifyService fieldClassifyService;

    @Autowired
    FieldClassifyUsageCountRepository fieldClassifyUsageCountRepository;

    @Autowired
    EasyUtil easyUtil;

    @Override
    public String FileDeidentify(HttpServletRequest rq,MultiValueMap<String, MultipartFile> files,AnonymizeConfigure anonymizeConfigure) throws Exception {

        //准备文件暂存路径
        String time=String.valueOf(System.currentTimeMillis());
        String savePath =rq.getSession().getServletContext().getRealPath("/identityFiles/"+time);
        FileResolve.createFile(savePath);
        System.out.println(savePath);


        //文件处理
        for(String key:files.keySet()) {
            MultipartFile file = files.getFirst(key);
            String filename = file.getOriginalFilename();
            anonymizeConfigure.setTransposed(true);
            if(filename.endsWith(".csv")){
                DataHandle dataHandle=easyUtil.deidentify_run(FileResolve.readerCsv(file.getInputStream()),anonymizeConfigure);
                FileResolve.writerCsv(savePath+"\\"+anonymizeConfigure.getLevel()+filename,dataHandle.getData());
            }
            if(filename.endsWith(".xls")){
                ArrayList<ArrayList<ArrayList<String>>> data=FileResolve.readerXls(file.getInputStream());
                for (int i = 0; i <data.size()-1 ; i++){
                    DataHandle dataHandle=easyUtil.deidentify_run(data.get(i),anonymizeConfigure);
                    data.set(i,dataHandle.getData());
                }
                FileResolve.writerXlsx(savePath+"\\"+anonymizeConfigure.getLevel()+filename+"x",data);
            }
            if(filename.endsWith(".xlsx")){
                ArrayList<ArrayList<ArrayList<String>>> data=FileResolve.readerXlsx(file.getInputStream());
                for (int i = 0; i <data.size()-1 ; i++){
                    DataHandle dataHandle=easyUtil.deidentify_run(data.get(i),anonymizeConfigure);
                    data.set(i,dataHandle.getData());
                }
                FileResolve.writerXlsx(savePath+"\\"+anonymizeConfigure.getLevel()+filename,data);
            }
        }

        //处理文件打包存储
        ZipCompressor zc = new ZipCompressor(savePath+".zip");
        zc.compress(savePath);
        FileResolve.deleteFile(savePath);
        //延时1天清空zip 文件
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    FileResolve.deleteFile(savePath+".zip");
                } catch (UnsupportedEncodingException e) {e.printStackTrace();}
            }
        };
        Timer timer = new Timer();
        long delay = 24*60*60*1000;
        timer.schedule(task, delay);

        //表单使用记录
        FieldClassifyUsageCount fieldClassifyUsageCount=fieldClassifyUsageCountRepository.findByFormName(anonymizeConfigure.getFieldFormName());
        fieldClassifyUsageCount.setCount(fieldClassifyUsageCount.getCount()+1);
        fieldClassifyUsageCountRepository.save(fieldClassifyUsageCount);

        return "/identityFiles/"+time+".zip";
    }


}
