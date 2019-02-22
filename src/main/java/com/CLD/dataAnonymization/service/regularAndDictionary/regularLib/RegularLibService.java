package com.CLD.dataAnonymization.service.regularAndDictionary.regularLib;

import com.CLD.dataAnonymization.dao.h2.entity.RegularLib;

import java.util.List;

/**
 * @Description: 规则库操作服务
 * @Author CLD
 * @Date 2019/2/22 13:17
 */
public interface RegularLibService {

    /**
     * 生成新的规则库
     * @param libName
     * @param description
     * @param user
     * @return
     */
    public String createNewLib(String libName,String description,String user);

    /**
     * 修改规则库描述
     * @param old_libName
     * @param new_libName
     * @param new_description
     * @return
     */
    public String updataLib(String old_libName,String new_libName,String new_description);

    /**
     * 获得指定用户的规则库
     * @param user
     * @return
     */
    public List<RegularLib> getLibByUser(String user);
}
