package com.CLD.dataAnonymization.service.deidentifyTarget.fileDeidentify;

import com.CLD.dataAnonymization.model.AnonymizeConfigure;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 该类用于处理文件类型数据
 **/
@Service
public interface FileDeidentifyService {

    /**
     * 该方法对文件数据进行匿名化操作，并进行打包
     * @param re     文件流
     * @param rq     请求信息
     * @param anonymizeConfigure
     * @return 打包文件的地址
     * @throws Exception
     */
    public String FileDeidentify(MultipartHttpServletRequest re, HttpServletRequest rq, AnonymizeConfigure anonymizeConfigure)throws Exception;

    /**
     * 获得原始配置
     * @return
     */
    public AnonymizeConfigure getAnonymizeConfigure();
}
