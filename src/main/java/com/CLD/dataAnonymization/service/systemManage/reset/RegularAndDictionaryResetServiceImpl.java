package com.CLD.dataAnonymization.service.systemManage.reset;

import com.CLD.dataAnonymization.service.regularAndDictionary.dictionary.DictionaryService;
import com.CLD.dataAnonymization.service.regularAndDictionary.regular.RegularService;
import com.CLD.dataAnonymization.service.regularAndDictionary.regularLib.RegularLibService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/22 14:17
 */
@Service
public class RegularAndDictionaryResetServiceImpl implements RegularAndDictionaryResetService {

    @Value("${package.jar.name}")
    private String jarName;

    @Value("${dictionary.name}")
    private String dictionaryName;

    @Value("${dictionary.in.path}")
    private String inDictionaryPath;

    @Value("${regualr.in.path}")
    private String inRegularPath;

    @Value("${regular.name}")
    private String regularName;

    @Autowired
    RegularLibService regularLibService;

    @Autowired
    DictionaryService dictionaryService;

    @Autowired
    RegularService regularService;

    @Override
    public Boolean RegularAndDictionaryReset() throws FileNotFoundException {

        //初始化规则库信息
        regularLibService.createNewLib("original","系统规则库","original");

        //初始化字典信息
        dictionaryService.deleteLib("original");

        dictionaryService.initDictionary(dictionaryName,"original","系统字典",inDictionaryPath+"/"+dictionaryName);

        //初始化规则信息
        regularService.deleteLib("original");

        regularService.initRegular("original",inRegularPath+"/"+regularName);

        return true;
    }
}
