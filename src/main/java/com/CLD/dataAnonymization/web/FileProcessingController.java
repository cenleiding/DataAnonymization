package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.model.AnonymizeConfigure;
import com.CLD.dataAnonymization.service.deidentifyTarget.fileDeidentify.FileDeidentifyService;
import com.CLD.dataAnonymization.service.systemManage.userIp.UserIp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @RequestMapping(value = "")
    public String FileProcessing(){
        return "FileProcessing";
    }

    /**
     * 对上传文件进行存储
     * @param re
     * @param re,rq,level
     * @throws Exception
     */
    @RequestMapping(value="/filecontent",method= RequestMethod.POST)
    @ResponseBody
    public Map<String,String> fileContentUpload (MultipartHttpServletRequest re,
                                  HttpServletRequest rq) throws Exception{
        log.info(userIp.getIp()+"使用文件匿名化接口");
        Map<String,String> out=new HashMap<String,String>();
        out.put("url",fileDeidentifyService.FileDeidentify(re,rq));
        return out;
    }

    @RequestMapping("/getAnonymizeConfigure")
    @ResponseBody
    public AnonymizeConfigure getAnonymizeConfigure(){
        return fileDeidentifyService.getAnonymizeConfigure();
    }
}
