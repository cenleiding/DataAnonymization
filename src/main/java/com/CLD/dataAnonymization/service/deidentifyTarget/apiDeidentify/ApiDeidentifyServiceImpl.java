package com.CLD.dataAnonymization.service.deidentifyTarget.apiDeidentify;

import com.CLD.dataAnonymization.dao.h2.entity.FieldClassifyUsageCount;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyUsageCountRepository;
import com.CLD.dataAnonymization.model.AnonymizeConfigure;
import com.CLD.dataAnonymization.model.FieldInfo;
import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
import com.CLD.dataAnonymization.util.deidentifier.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @description：该类用于提供API数据匿名化服务
 * @Author CLD
 * @Date 2018/5/27 13:57
 **/
@Service
public class ApiDeidentifyServiceImpl implements ApiDeidentifyService{

    @Autowired
    DataParseService dataParseService;

    @Autowired
    FieldClassifyService fieldClassifyService;

    @Autowired
    FieldClassifyUsageCountRepository fieldClassifyUsageCountRepository;

    @Override
    public ArrayList<HashMap<String, String>> ApiDataDeidentify(HttpServletRequest req, AnonymizeConfigure anonymizeConfigure) {
        //准备匿名字段表
        List<FieldInfo> fieldInfoList=fieldClassifyService.getFieldByFromName(anonymizeConfigure.getFieldFormName());
        ArrayList<ArrayList<String>> fieldList=new ArrayList<ArrayList<String>>();
        for(FieldInfo fieldInfo:fieldInfoList){
            ArrayList<String> field=new ArrayList<String>();
            field.add(fieldInfo.getFieldName());
            field.add(fieldInfo.getFieldType());
            fieldList.add(field);
        }
        //匿名化配置
        Configuration configuration=new Configuration();
        if(anonymizeConfigure.getLevel().equals("Level1"))
            configuration.setLevel(Configuration.AnonymousLevel.Level1);
        else
            configuration.setLevel(Configuration.AnonymousLevel.Level2);
        configuration.setEncryptPassword(anonymizeConfigure.getEncryptPassword());
        configuration.setK_big(Integer.valueOf(anonymizeConfigure.getK_big()));
        configuration.setK_small(Integer.valueOf(anonymizeConfigure.getK_small()));
        configuration.setMicroaggregation(Integer.valueOf(anonymizeConfigure.getMicroaggregation()));
        configuration.setNoiseScope_big(Double.valueOf(anonymizeConfigure.getNoiseScope_big()));
        configuration.setNoiseScope_small(Double.valueOf(anonymizeConfigure.getNoiseScope_small()));
        configuration.setSuppressionLimit_level1(Double.valueOf(anonymizeConfigure.getSuppressionLimit_level1()));
        configuration.setSuppressionLimit_level2(Double.valueOf(anonymizeConfigure.getSuppressionLimit_level2()));
        configuration.setT(Double.valueOf(anonymizeConfigure.getT()));

        //获取数据
        ArrayList<ArrayList<String>> data=dataParseService.requestDataToArrayList(req);
        ArrayList<HashMap<String,String>> outData=new ArrayList<HashMap<String, String>>();

        //匿名化
        DataHandle dataHandle=new DataHandle(data);
        dataHandle.setFieldList(fieldList);
        Anonymizer anonymizer=new Anonymizer(dataHandle,configuration);
        data=anonymizer.anonymize();

        //表单使用记录
        FieldClassifyUsageCount fieldClassifyUsageCount=fieldClassifyUsageCountRepository.findByFormName(anonymizeConfigure.getFieldFormName());
        fieldClassifyUsageCount.setCount(fieldClassifyUsageCount.getCount()+1);
        fieldClassifyUsageCountRepository.save(fieldClassifyUsageCount);


        for(int i=1;i<data.size();i++){
            HashMap<String,String> map=new HashMap<String, String>();
            for(int j=0;j<data.get(0).size();j++){
                map.put(data.get(0).get(j),data.get(i).get(j));
            }
            outData.add(map);
        }
        return outData;
    }
}
