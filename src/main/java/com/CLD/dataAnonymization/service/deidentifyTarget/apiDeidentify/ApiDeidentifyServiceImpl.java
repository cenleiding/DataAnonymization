package com.CLD.dataAnonymization.service.deidentifyTarget.apiDeidentify;

import com.CLD.dataAnonymization.dao.h2.entity.FieldClassifyUsageCount;
import com.CLD.dataAnonymization.dao.h2.repository.DictionaryrRepository;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyUsageCountRepository;
import com.CLD.dataAnonymization.dao.h2.repository.RegularRepository;
import com.CLD.dataAnonymization.model.AnonymizeConfigure;
import com.CLD.dataAnonymization.service.deidentifyTarget.EasyUtil;
import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

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

    @Autowired
    DictionaryrRepository dictionaryrRepository;

    @Autowired
    RegularRepository regularRepository;

    @Autowired
    EasyUtil easyUtil;

    @Override
    public ArrayList<HashMap<String, String>> ApiDataDeidentify(HttpServletRequest req, AnonymizeConfigure anonymizeConfigure) {

        //获取数据
        ArrayList<ArrayList<String>> data=dataParseService.requestDataToArrayList(req);
        ArrayList<HashMap<String,String>> outData=new ArrayList<HashMap<String, String>>();

        //匿名化
        anonymizeConfigure.setTransposed(true);
        data=easyUtil.deidentify_run(data,anonymizeConfigure).getData();

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
