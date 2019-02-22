package com.CLD.dataAnonymization.service.regularAndDictionary.dictionary;

import com.CLD.dataAnonymization.dao.h2.entity.Dictionary;
import com.CLD.dataAnonymization.dao.h2.repository.DictionaryrRepository;
import com.CLD.dataAnonymization.dao.h2.repository.RegularLibRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.transaction.Transactional;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/22 14:32
 */
@Service
public class DictionaryServiceImpl implements DictionaryService {

    @Value("${package.jar.name}")
    private String jarName;

    @Autowired
    RegularLibRepository regularLibRepository;

    @Autowired
    DictionaryrRepository dictionaryrRepository;

    @Override
    public Boolean saveDictionaryFile(MultipartHttpServletRequest re) {
        return null;
    }

    @Override
    public Boolean saveDictionary2Db(String fileName, String libName, String description) {

        String Path= new Object(){
            public String get(){
                return this.getClass().getClassLoader().getResource("").getPath();
            }
        }.get().replaceAll("target/classes/","")
                .replaceAll(jarName+"!/BOOT-INF/classes!/","")
                .replaceAll("file:","");

        Dictionary dictionary = new Dictionary();
        dictionary.setDescription(description);
        dictionary.setFileName(fileName);
        dictionary.setLibName(libName);
        dictionary.setUploadTime(new java.sql.Date(new java.util.Date().getTime()));
        dictionary.setUrl(Path+libName+"/"+fileName);
        dictionaryrRepository.saveAndFlush(dictionary);

        return true;
    }

    @Transactional
    @Override
    public Boolean deleteLib(String libName) {
        dictionaryrRepository.deleteByLibName(libName);
        dictionaryrRepository.flush();
        return true;
    }

    @Override
    public Boolean deleteDictionary(String fileName, String libName) {
        return null;
    }
}
