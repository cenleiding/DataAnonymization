package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.deidentifyTarget.fileDeidentify.FileDeidentifyService;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 该控制器用于文件在线处理
 * @Author CLD
 * @Date 2018/4/13 11:31
 **/
@Controller
public class FileProcessingController {

    @Autowired
    FileDeidentifyService fileDeidentifyService;

    @RequestMapping(value = "/FileProcessing")
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
    public JSONArray fileContentUpload (MultipartHttpServletRequest re,
                                        HttpServletRequest rq,
                                        @RequestParam("level") String level,
                                        @RequestParam(value = "fieldFromName",defaultValue = "Original",required = false) String fieldFromName) throws Exception{
        JSONArray jsonArray=new JSONArray();
        jsonArray.add(fileDeidentifyService.FileDeidentify(re,rq,level,fieldFromName));
        return jsonArray;
    }
}
