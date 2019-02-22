package com.CLD.dataAnonymization.service.systemManage.reset;

import com.CLD.dataAnonymization.service.regularAndDictionary.dictionary.DictionaryService;
import com.CLD.dataAnonymization.service.regularAndDictionary.regular.RegularService;
import com.CLD.dataAnonymization.service.regularAndDictionary.regularLib.RegularLibService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/22 14:17
 */
@Service
public class RegularAndDictionaryResetServiceImpl implements RegularAndDictionaryResetService {

    @Value("${dictionary.name}")
    private String dictionaryName;

    @Autowired
    RegularLibService regularLibService;

    @Autowired
    DictionaryService dictionaryService;

    @Autowired
    RegularService regularService;

    @Override
    public Boolean RegularAndDictionaryReset() {

        //初始化规则库信息
        regularLibService.createNewLib("original","系统规则库","original");

        //初始化字典信息
        dictionaryService.deleteLib("original");

        dictionaryService.saveDictionary2Db(dictionaryName,"original","系统自带字典");

        //加载规则信息，file2Db
        regularService.file2Db();

        return null;
    }
}
