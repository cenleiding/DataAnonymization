package com.CLD.dataAnonymization.service.regularAndDictionary.regularLib;

import com.CLD.dataAnonymization.dao.h2.entity.RegularLib;
import com.CLD.dataAnonymization.dao.h2.repository.RegularLibRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/22 13:24
 */
@Service
public class RegularLibServiceImpl implements RegularLibService {

    @Autowired
    RegularLibRepository regularLibRepository;

    @Override
    public String createNewLib(String libName, String description, String user) {
        List<String> libNameList = regularLibRepository.getLibName();
        if(libNameList.contains(libName)) return "规则库已存在！";
        RegularLib regularLib = new RegularLib();
        regularLib.setLibName(libName);
        regularLib.setDescription(description);
        regularLib.setUser(user);
        regularLib.setCreateTime(new java.sql.Date(new Date().getTime()));
        regularLib.setChangeTime(new java.sql.Date(new Date().getTime()));
        regularLibRepository.saveAndFlush(regularLib);
        return "创建成功！";
    }

    @Override
    public String updataLib(String old_libName, String new_libName, String new_description) {
        List<String> libNameList = regularLibRepository.getLibName();
        if (!libNameList.contains(old_libName)) return "原规则库不存在！";
        if ((!new_libName.equals(old_libName))&&(libNameList.contains(new_libName))) return "规则库名已存在！";
        RegularLib regularLib = regularLibRepository.findByLibName(old_libName);
        regularLib.setDescription(new_description);
        regularLib.setChangeTime(new java.sql.Date(new Date().getTime()));
        regularLib.setLibName(new_libName);
        regularLibRepository.saveAndFlush(regularLib);
        return "更新成功！";
    }

    @Override
    public List<RegularLib> getLibByUser(String user) {
        return regularLibRepository.findAllByUser(user);
    }

    @Override
    public boolean deleteLib(String libName) {
        regularLibRepository.deleteByLibName(libName);
        regularLibRepository.flush();
        return true;
    }

    @Override
    public boolean updateLastChangeTime(String libName) {
        RegularLib regularLib = regularLibRepository.findByLibName(libName);
        regularLib.setChangeTime(new java.sql.Date(new Date().getTime()));
        regularLibRepository.saveAndFlush(regularLib);
        return true;
    }


}
