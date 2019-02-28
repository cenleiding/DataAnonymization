package com.CLD.dataAnonymization.service.regularAndDictionary.testRun;

import com.CLD.dataAnonymization.dao.h2.entity.Dictionary;
import com.CLD.dataAnonymization.dao.h2.entity.Regular;
import com.CLD.dataAnonymization.dao.h2.repository.DictionaryrRepository;
import com.CLD.dataAnonymization.dao.h2.repository.RegularRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.CLD.dataAnonymization.util.deidentifier.algorithm.Unstructured.unstructured_NER;
import static com.CLD.dataAnonymization.util.deidentifier.algorithm.Unstructured.unstructured_Re;
import static com.CLD.dataAnonymization.util.deidentifier.algorithm.Unstructured.unstructured_dic;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/28 9:40
 */
@Service
public class TestRunServiceImpl implements TestRunService {

    @Autowired
    DictionaryrRepository dictionaryrRepository;

    @Autowired
    RegularRepository regularRepository;

    @Override
    public HashMap<String,String> testSimpleDictionary(String libName, String fileName, String content ) {
        HashMap<String,String> result = new HashMap<String,String>();
        Dictionary dictionary = dictionaryrRepository.findByLibNameAndFileName(libName,fileName);
        String[] words = dictionary.getContent().split(",");
        for(String word : words){
            if(content.indexOf(word)!= -1)
                result.put(word,"***");
        }

        return result;
    }

    @Override
    public HashMap<String,String> testSimpleRegular(String area, String aims, String content) {
        HashMap<String,String> result = new HashMap<String,String>();
        Pattern p1 = Pattern.compile(area);
        Matcher m1 = p1.matcher(content);
        while (m1.find()){
            Pattern p2 = Pattern.compile(aims);
            Matcher m2 = p2.matcher(m1.group());
            m2.find();
            result.put(m1.group(),m1.group().replace(m2.group(),"***"));
        }
        return result;
    }

    @Override
    public HashMap<String,String> testAll(boolean xtzd, boolean xtgz, boolean wdzd, boolean wdgz, boolean jqxx, String libName, String content) {
        ArrayList<String> dic = new ArrayList<String>();
        ArrayList<HashMap<String,String>> re = new ArrayList<HashMap<String, String>>();
        ArrayList<String> contentList = new ArrayList<String>();
        contentList.add(content);
        HashMap<String,String> outResult = new HashMap<String,String>();

        // 加载系统字典
        if(xtzd == true){
            List<Dictionary> dictionaryList = dictionaryrRepository.findByLibName("original");
            for(Dictionary dictionary:dictionaryList){
                String[] word = dictionary.getContent().split(",");
                for(String w:word)
                    dic.add(w);
            }
        }

        //加载我的字典
        if(wdzd == true){
            List<Dictionary> dictionaryList = dictionaryrRepository.findByLibName(libName);
            for(Dictionary dictionary:dictionaryList){
                String[] word = dictionary.getContent().split(",");
                for(String w:word)
                    dic.add(w);
            }
        }

        //加载系统规则
        if(xtgz == true){
            List<Regular> regularList = regularRepository.findByLibName("original");
            for(Regular regular:regularList){
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("area",regular.getArea());
                hashMap.put("aims",regular.getAims());
                re.add(hashMap);
            }
        }

        //加载我的规则
        if(wdgz == true){
            List<Regular> regularList = regularRepository.findByLibName(libName);
            for(Regular regular:regularList){
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("area",regular.getArea());
                hashMap.put("aims",regular.getAims());
                re.add(hashMap);
            }
        }


        // 利用机器学习获取隐私信息
        if(jqxx==true){
            HashMap<String,HashSet<String>> ner_result = unstructured_NER(contentList);
            for(String key:ner_result.keySet()){
                for (String s : ner_result.get(key))
                    outResult.put(s,"***");
            }
        }

        //利用规则获取隐私信息
        HashMap<String,String> re_result = unstructured_Re(contentList,re);
        outResult.putAll(re_result);

        //利用字典获取隐私信息
        HashMap<String,String> dic_result = unstructured_dic(contentList,dic);
        outResult.putAll(dic_result);

        return outResult;
    }


}
