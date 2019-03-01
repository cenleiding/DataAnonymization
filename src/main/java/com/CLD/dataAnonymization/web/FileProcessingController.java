package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.model.AnonymizeConfigure;
import com.CLD.dataAnonymization.service.deidentifyTarget.EasyUtil;
import com.CLD.dataAnonymization.service.deidentifyTarget.fileDeidentify.FileDeidentifyService;
import com.CLD.dataAnonymization.service.systemManage.userIp.UserIp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:该控制器用于文件在线处理
 * @Author CLD
 * @Date 2018/4/13 11:31
 **/

@Controller
@RequestMapping("/FileProcessing")
@Slf4j
public class FileProcessingController {

    @Autowired
    FileDeidentifyService fileDeidentifyService;

    @Autowired
    UserIp userIp;

    @Autowired
    EasyUtil easyUtil;

    @RequestMapping(value = "")
    public String FileProcessing(){
        return "FileProcessing";
    }

    /**
     * 对上传文件进行处理存储
     * @param re
     * @param re,rq,level
     * @throws Exception
     */
    @RequestMapping(value="/filecontent",method= RequestMethod.POST)
    @ResponseBody
    public Map<String,String> fileContentUpload (MultipartHttpServletRequest re,HttpServletRequest rq) throws Exception{
        log.info(userIp.getIp()+"使用文件匿名化接口");
        //获得配置文件
        Map<String,String[]> parameterMap=re.getParameterMap();
        AnonymizeConfigure anonymizeConfigure=new AnonymizeConfigure();
        anonymizeConfigure.setFieldFormName(parameterMap.get("fieldFormName")[0]);
        anonymizeConfigure.setLevel(parameterMap.get("level")[0]);
        anonymizeConfigure.setRegularLib(parameterMap.get("regularLib")[0]);
        anonymizeConfigure.setEncryptPassword(parameterMap.get("encryptPassword")[0]);
        anonymizeConfigure.setK_big(parameterMap.get("k_big")[0]);
        anonymizeConfigure.setK_small(parameterMap.get("k_small")[0]);
        anonymizeConfigure.setMicroaggregation(parameterMap.get("microaggregation")[0]);
        anonymizeConfigure.setNoiseScope_big(parameterMap.get("noiseScope_big")[0]);
        anonymizeConfigure.setNoiseScope_small(parameterMap.get("noiseScope_small")[0]);
        anonymizeConfigure.setSuppressionLimit_level1(parameterMap.get("suppressionLimit_level1")[0]);
        anonymizeConfigure.setSuppressionLimit_level2(parameterMap.get("suppressionLimit_level2")[0]);
        anonymizeConfigure.setT(parameterMap.get("t")[0]);
        // 获取文件
        MultiValueMap<String, MultipartFile> files=re.getMultiFileMap();

        Map<String,String> out=new HashMap<String,String>();
        out.put("url",fileDeidentifyService.FileDeidentify(rq,files,anonymizeConfigure));
        return out;
    }

    @RequestMapping("/getAnonymizeConfigure")
    @ResponseBody
    public AnonymizeConfigure getAnonymizeConfigure(){
        return easyUtil.getAnonymizeConfigure();
    }
}
