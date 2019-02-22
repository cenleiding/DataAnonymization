package com.CLD.dataAnonymization.service.regularAndDictionary.regular;

import com.CLD.dataAnonymization.dao.h2.entity.Regular;
import com.CLD.dataAnonymization.dao.h2.repository.RegularRepository;
import com.CLD.dataAnonymization.util.deidentifier.io.FileResolve;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/22 15:18
 */
@Service
public class RegularServiceImpl implements RegularService {

    @Value("${package.jar.name}")
    private String jarName;

    @Value("${regular.out.path}")
    private String outRegularPath;

    @Value("${regular.name}")
    private String regularName;

    @Autowired
    RegularRepository regularRepository;

    @Override
    public Boolean file2Db() {

        String FilePath_mapping=new Object() {
            public String get(){
                return this.getClass().getClassLoader().getResource("").getPath();
            }
        }.get().replaceAll("target/classes/","")
                .replaceAll(jarName+"!/BOOT-INF/classes!/","")
                .replaceAll("file:","");
        String path=FilePath_mapping+outRegularPath+"/"+regularName;
        JSONArray jsonArray=new JSONArray();
        try {
            jsonArray= FileResolve.readerArrayJson(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        List<Regular> regularList = new ArrayList<Regular>();
        for(int i=0;i<jsonArray.size();i++){
            Regular regular = new Regular();
            regular.setLibName("original");
            regular.setAims(jsonArray.getJSONObject(i).getString("aims"));
            regular.setArea(jsonArray.getJSONObject(i).getString("area"));
            regularList.add(regular);
        }
        regularRepository.saveAll(regularList);

        return true;
    }
}
