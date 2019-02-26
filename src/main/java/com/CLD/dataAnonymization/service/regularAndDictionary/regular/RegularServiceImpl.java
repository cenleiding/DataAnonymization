package com.CLD.dataAnonymization.service.regularAndDictionary.regular;

import com.CLD.dataAnonymization.dao.h2.entity.Regular;
import com.CLD.dataAnonymization.dao.h2.repository.RegularRepository;
import com.CLD.dataAnonymization.util.deidentifier.io.FileResolve;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
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
    public Boolean deleteLib(String libName) {
        regularRepository.deleteByLibName(libName);
        return true;
    }

    @Override
    public Boolean initRegular(String libName, String Path) throws FileNotFoundException {
        InputStream is=new Object(){
            public InputStream get(){
                return this.getClass().getClassLoader().getResourceAsStream(Path);
            }
        }.get();
        JSONArray jsonArray = FileResolve.readerArrayJson(is);
        List<Regular> regularList = new ArrayList<Regular>();
        for(int i=0;i<jsonArray.size();i++){
            Regular regular = new Regular();
            regular.setLibName(libName);
            regular.setAims(jsonArray.getJSONObject(i).getString("aims"));
            regular.setArea(jsonArray.getJSONObject(i).getString("area"));
            regularList.add(regular);
        }
        regularRepository.saveAll(regularList);
        return true;
    }

    @Override
    public List<Regular> getRegularByLibName(String libName) {
        return regularRepository.findByLibName(libName);
    }

    @Override
    public void deleteById(Long id) {
        regularRepository.deleteById(id);
    }

    @Override
    public void createNewRegular(String libName, String area, String aims) {
        Regular regular = new Regular();
        regular.setArea(area);
        regular.setAims(aims);
        regular.setLibName(libName);
        regularRepository.save(regular);
    }

    @Override
    public void changeRegular(Regular regular) {
        regularRepository.save(regular);
    }
}
