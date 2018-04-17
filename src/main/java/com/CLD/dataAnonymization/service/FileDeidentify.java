package com.CLD.dataAnonymization.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 该类用于处理文件类型数据
 * @Author CLD
 * @Date 2018/4/13 17:04
 **/
@Service
public interface FileDeidentify {

    /**
     * 该方法对文件数据进行匿名化操作，并进行打包
     * @param re
     * @param level 匿名化程度(L,S)
     * @return 打包文件的地址
     */
    public String FileDeidentify(MultipartHttpServletRequest re, HttpServletRequest rq,String level) throws Exception;

}
