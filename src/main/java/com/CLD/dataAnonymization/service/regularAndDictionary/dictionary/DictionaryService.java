package com.CLD.dataAnonymization.service.regularAndDictionary.dictionary;

import org.springframework.web.multipart.MultipartHttpServletRequest;

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
    public Boolean saveDictionaryFile(MultipartHttpServletRequest re);

    /**
     * 对上传文件信息进行存储
     * 自动生成路径：~/库名/文件名
     * @param fileName
     * @param libName
     * @return
     */
    public Boolean saveDictionary2Db(String fileName, String libName, String description);


    /**
     * 删除指定的规则库
     * @param libName
     * @return
     */
    public Boolean deleteLib(String libName);

    /**
     * 对指定字典进行删除，包括文件和数据库信息
     * @param fileName
     * @param libName
     * @return
     */
    public Boolean deleteDictionary(String fileName, String libName);

}
