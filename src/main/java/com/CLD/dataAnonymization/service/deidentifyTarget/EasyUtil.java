package com.CLD.dataAnonymization.service.deidentifyTarget;

import com.CLD.dataAnonymization.dao.h2.entity.Dictionary;
import com.CLD.dataAnonymization.dao.h2.entity.Regular;
import com.CLD.dataAnonymization.dao.h2.repository.DictionaryrRepository;
import com.CLD.dataAnonymization.dao.h2.repository.RegularRepository;
import com.CLD.dataAnonymization.model.AnonymizeConfigure;
import com.CLD.dataAnonymization.model.FieldInfo;
import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
import com.CLD.dataAnonymization.util.deidentifier.Anonymizer;
import com.CLD.dataAnonymization.util.deidentifier.Configuration;
import com.CLD.dataAnonymization.util.deidentifier.DataHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: 一个工具类
 * @Author CLD
 * @Date 2019/3/1 16:38
 */
@Service
public class EasyUtil {

    @Autowired
    FieldClassifyService fieldClassifyService;

    @Autowired
    DictionaryrRepository dictionaryrRepository;

    @Autowired
    RegularRepository regularRepository;

    @Value("${ner.url}")
    private String ner_url;

    @Value("${ner.corePoolSize}")
    private Integer ner_corePoolSize;

    @Value("${ner.maximumPoolSize}")
    private Integer ner_maximumPoolSize;

    @Value("${ner.batchSize}")
    private Integer ner_batchSize;

    /**
     * 匿名化快速调用
     * @param data
     * @param anonymizeConfigure
     */
    public DataHandle deidentify_run(ArrayList<ArrayList<String>> data,
                               AnonymizeConfigure anonymizeConfigure){

        DataHandle dataHandle=new DataHandle(data);
        //准备匿名字段表
        dataHandle.setFieldList(getFieldList(anonymizeConfigure));
        //准备非结构化字典
        dataHandle.setDictionary(getDictionaryList(anonymizeConfigure));
        //准备非结构化规则
        dataHandle.setRegular(getRegularList(anonymizeConfigure));
        //匿名化配置
        Configuration configuration= getConfiguration(anonymizeConfigure);

        Anonymizer anonymizer=new Anonymizer(dataHandle,configuration);

        anonymizer.anonymize();

        return dataHandle;
    }

    /**
     * 获得指定匿名字段表
     * @param anonymizeConfigure
     * @return
     */
    public  ArrayList<ArrayList<String>> getFieldList(AnonymizeConfigure anonymizeConfigure){
        ArrayList<ArrayList<String>> fieldList=new ArrayList<ArrayList<String>>();
        List<FieldInfo> fieldInfoList=fieldClassifyService.getFieldByFromName(anonymizeConfigure.getFieldFormName());
        for(FieldInfo fieldInfo:fieldInfoList){
            ArrayList<String> field=new ArrayList<String>();
            field.add(fieldInfo.getFieldName());
            field.add(fieldInfo.getFieldType());
            fieldList.add(field);
        }
        return fieldList;
    }

    /**
     * 获得指定字典表
     * @param anonymizeConfigure
     * @return
     */
    public ArrayList<String> getDictionaryList(AnonymizeConfigure anonymizeConfigure){
        ArrayList<String> dictionary = new ArrayList<String>();
        List<Dictionary> dictionaryList = dictionaryrRepository.findByLibName("original");
        if(!anonymizeConfigure.getRegularLib().equals("original")){
            dictionaryList.addAll(dictionaryrRepository.findByLibName(anonymizeConfigure.getRegularLib()));
        }
        for(Dictionary d:dictionaryList){
            String[] content = d.getContent().split(",");
            for(String s:content) {
                dictionary.add(s);
            }
        }
        return dictionary;
    }

    /**
     * 获得指定规则表
     * @param anonymizeConfigure
     * @return
     */
    public ArrayList<HashMap<String,String>> getRegularList(AnonymizeConfigure anonymizeConfigure){
        ArrayList<HashMap<String,String>> regular = new ArrayList<HashMap<String,String>>();
        List<Regular> regularList = regularRepository.findByLibName("original");
        if(!anonymizeConfigure.getRegularLib().equals("original")){
            regularList.addAll(regularRepository.findByLibName(anonymizeConfigure.getRegularLib()));
        }
        for(Regular re:regularList){
            HashMap<String,String> r = new HashMap<String, String>();
            r.put("area",re.getArea());
            r.put("aims",re.getAims());
            regular.add(r);
        }
        return regular;
    }

    /**
     * 获得匿名化配置
     * @param anonymizeConfigure
     * @return
     */
    public Configuration getConfiguration(AnonymizeConfigure anonymizeConfigure){
        Configuration configuration=new Configuration();
        if(anonymizeConfigure.getLevel().equals("Level1"))
            configuration.setLevel(Configuration.AnonymousLevel.Level1);
        else
            configuration.setLevel(Configuration.AnonymousLevel.Level2);
        configuration.setK_big(Integer.valueOf(anonymizeConfigure.getK_big()));
        configuration.setK_small(Integer.valueOf(anonymizeConfigure.getK_small()));
        configuration.setMicroaggregation(Integer.valueOf(anonymizeConfigure.getMicroaggregation()));
        configuration.setNoiseScope_big(Double.valueOf(anonymizeConfigure.getNoiseScope_big()));
        configuration.setNoiseScope_small(Double.valueOf(anonymizeConfigure.getNoiseScope_small()));
        configuration.setSuppressionLimit_level1(Double.valueOf(anonymizeConfigure.getSuppressionLimit_level1()));
        configuration.setSuppressionLimit_level2(Double.valueOf(anonymizeConfigure.getSuppressionLimit_level2()));
        configuration.setT(Double.valueOf(anonymizeConfigure.getT()));
        configuration.setNer_url(ner_url);
        configuration.setTransposed(anonymizeConfigure.getTransposed());
        configuration.setNer_corePoolSize(ner_corePoolSize);
        configuration.setNer_maximumPoolSize(ner_maximumPoolSize);
        configuration.setNer_batchSize(ner_batchSize);
        return configuration;
    }

    public AnonymizeConfigure getAnonymizeConfigure() {
        AnonymizeConfigure anonymizeConfigure=new AnonymizeConfigure();
        Configuration configuration=new Configuration();
        anonymizeConfigure.setFieldFormName("");
        anonymizeConfigure.setK_big(String.valueOf(configuration.getK_big()));
        anonymizeConfigure.setK_small(String.valueOf(configuration.getK_small()));
        anonymizeConfigure.setLevel(String.valueOf(configuration.getLevel()));
        anonymizeConfigure.setMicroaggregation(String.valueOf(configuration.getMicroaggregation()));
        anonymizeConfigure.setNoiseScope_big(String.valueOf(configuration.getNoiseScope_big()));
        anonymizeConfigure.setNoiseScope_small(String.valueOf(configuration.getNoiseScope_small()));
        anonymizeConfigure.setSuppressionLimit_level1(String.valueOf(configuration.getSuppressionLimit_level1()));
        anonymizeConfigure.setSuppressionLimit_level2(String.valueOf(configuration.getSuppressionLimit_level2()));
        anonymizeConfigure.setT(String.valueOf(configuration.getT()));
        return anonymizeConfigure;
    }

}
