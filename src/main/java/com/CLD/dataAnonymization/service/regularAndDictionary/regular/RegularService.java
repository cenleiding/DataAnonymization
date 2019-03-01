package com.CLD.dataAnonymization.service.regularAndDictionary.regular;

import com.CLD.dataAnonymization.dao.h2.entity.Regular;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * @Description: 规则操作
 * @Author CLD
 * @Date 2019/2/22 15:17
 */
public interface RegularService {

    /**
     * 删除指定规则库的规则
     * @param libName
     * @return
     */
    public Boolean deleteLib(String libName);

    /**
     * 初始化系统规则库
     * @param libName
     * @param Path
     * @return
     * @throws FileNotFoundException
     */
    public Boolean initRegular(String libName,String Path) throws FileNotFoundException;

    /**
     * 根据规则库名获取规则
     * @param libName
     * @return
     */
    public List<Regular> getRegularByLibName(String libName);

    /**
     * 删除单条规则
     * @param id
     */
    public void deleteById(Long id);

    /**
     * 新建规则
     * @param libName
     * @param area
     * @param aims
     */
    public void createNewRegular(String libName,String area,String aims);

    /**
     * 修改规则
     * @param regular
     */
    public void changeRegular(Regular regular);

    /**
     * 更新规则所属规则库名
     * @param old_libName
     * @param new_libName
     * @return
     */
    public Boolean updateLibName(String old_libName,String new_libName);
}
