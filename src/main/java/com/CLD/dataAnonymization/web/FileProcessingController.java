package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.FileDeidentify;
import com.CLD.dataAnonymization.service.FileDeidentifyImpl;
import com.CLD.dataAnonymization.util.deidentifier.FileResolve;
import com.CLD.dataAnonymization.util.deidentifier.ZipCompressor;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.apache.coyote.http11.Constants.a;

/**
 * 该控制器用于文件在线处理
 * @Author CLD
 * @Date 2018/4/13 11:31
 **/
@Controller
public class FileProcessingController {

    @Autowired
    FileDeidentify fileDeidentify;

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
    public JSONArray fileContentUpload (MultipartHttpServletRequest re,HttpServletRequest rq, @RequestParam("level") String level) throws Exception{
        JSONArray jsonArray=new JSONArray();
        jsonArray.add(fileDeidentify.FileDeidentify(re,rq,level));
        return jsonArray;
    }
}
