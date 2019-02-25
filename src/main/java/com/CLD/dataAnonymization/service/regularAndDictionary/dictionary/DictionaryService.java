package com.CLD.dataAnonymization.service.regularAndDictionary.dictionary;

import com.CLD.dataAnonymization.dao.h2.entity.Dictionary;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @Description: 字典操作
 * @Author CLD
 * @Date 2019/2/22 14:31
 */
public interface DictionaryService {


    /**
     * 将上传的文件进行解析存储
     * @param re
     * @return
     */
    public String uploadDictionaryFile(MultipartHttpServletRequest re) throws IOException;

    /**
     * 初始化系统字典
     * @param fileName
     * @param libName
     * @param description
     * @param Path
     * @return
     */
    public Boolean initDictionary(String fileName, String libName, String description,String Path);


    /**
     * 删除指定的规则库
     * @param libName
     * @return
     */
    public Boolean deleteLib(String libName);

    /**
     * 对指定字典进行删除
     * @param fileName
     * @param libName
     * @return
     */
    public Boolean deleteDictionary(String fileName, String libName);

    /**
     * 更新字典所属规则库名
     * @param old_libName
     * @param new_libName
     * @return
     */
    public String updateLibName(String old_libName,String new_libName);

    /**
     * 根据libName获取字典列表
     * @param libName
     * @return
     */
    public List<Dictionary> getDictionaryByLibName(String libName);

    /**
     * 下载指定的字典
     * @param rq
     * @param fileName
     * @param libName
     * @return
     */
    public String downloadDictionary(HttpServletRequest rq,String fileName, String libName) throws UnsupportedEncodingException;

}
