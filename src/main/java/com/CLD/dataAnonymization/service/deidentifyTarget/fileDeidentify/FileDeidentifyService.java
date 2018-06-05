package com.CLD.dataAnonymization.service.deidentifyTarget.fileDeidentify;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 该类用于处理文件类型数据
 * @Author CLD
 * @Date 2018/4/13 17:04
 **/
@Service
public interface FileDeidentifyService {

    /**
     * 该方法对文件数据进行匿名化操作，并进行打包
     * @param re    文件流
     * @param rq    请求信息
     * @param level 匿名化程度(L,S)
     * @return 打包文件的地址
     * @throws Exception
     */
    public String FileDeidentify(MultipartHttpServletRequest re, HttpServletRequest rq,String level) throws Exception;

    /**
     * 该方法对文件数据进行匿名化操作，并进行打包
     * @param re     文件流
     * @param rq     请求信息
     * @param level  匿名化程度(L,S)
     * @param fieldFromName 匿名字段表
     * @return 打包文件的地址
     * @throws Exception
     */
    public String FileDeidentify(MultipartHttpServletRequest re, HttpServletRequest rq, String level,String fieldFromName)throws Exception;

}
