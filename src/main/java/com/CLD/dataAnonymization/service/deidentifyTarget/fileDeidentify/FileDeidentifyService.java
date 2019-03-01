package com.CLD.dataAnonymization.service.deidentifyTarget.fileDeidentify;

import com.CLD.dataAnonymization.model.AnonymizeConfigure;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:该类用于处理文件类型数据
 * @Author CLD
 * @Date 2018/4/16 8:43
 **/
@Service
public interface FileDeidentifyService {

    /**
     * 该方法对文件数据进行匿名化操作，并进行打包
     * @param rq
     * @param files
     * @param anonymizeConfigure
     * @return 打包文件的地址
     * @throws Exception
     */
    public String FileDeidentify( HttpServletRequest rq, MultiValueMap<String, MultipartFile> files,AnonymizeConfigure anonymizeConfigure)throws Exception;

}
