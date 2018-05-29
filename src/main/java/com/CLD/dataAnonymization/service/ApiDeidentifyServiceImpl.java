package com.CLD.dataAnonymization.service;

import com.CLD.dataAnonymization.model.FieldInfo;
import com.CLD.dataAnonymization.util.deidentifier.IOAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author CLD
 * @Date 2018/5/27 13:57
 **/
@Service
public class ApiDeidentifyServiceImpl implements ApiDeidentifyService{

    @Autowired
    DataParseService dataParseService;

    @Autowired
    FieldClassifyService fieldClassifyService;

    @Override
    public ArrayList<HashMap<String,String>> ApiSafeHarbor(HttpServletRequest req, String fieldFromName) {
        List<FieldInfo> fieldInfoList=fieldClassifyService.getFieldByFromName(fieldFromName);
        ArrayList<ArrayList<String>> fieldList=new ArrayList<ArrayList<String>>();
        for(FieldInfo fieldInfo:fieldInfoList){
            ArrayList<String> field=new ArrayList<String>();
            field.add(fieldInfo.getFieldName());
            field.add(fieldInfo.getFieldType());
            fieldList.add(field);
        }
        ArrayList<ArrayList<String>> data=dataParseService.requestDataToArrayList(req);
        ArrayList<HashMap<String,String>> outData=new ArrayList<HashMap<String, String>>();
        data= IOAdapter.ToSafeHarbor(data,fieldList);
        for(int i=1;i<data.size();i++){
            HashMap<String,String> map=new HashMap<String, String>();
            for(int j=0;j<data.get(0).size();j++){
                map.put(data.get(0).get(j),data.get(i).get(j));
            }
            outData.add(map);
        }
        return outData;
    }

    @Override
    public ArrayList<HashMap<String,String>>  ApiLimitedSet(HttpServletRequest req,String fieldFromName) {
        List<FieldInfo> fieldInfoList=fieldClassifyService.getFieldByFromName(fieldFromName);
        ArrayList<ArrayList<String>> fieldList=new ArrayList<ArrayList<String>>();
        for(FieldInfo fieldInfo:fieldInfoList){
            ArrayList<String> field=new ArrayList<String>();
            field.add(fieldInfo.getFieldName());
            field.add(fieldInfo.getFieldType());
            fieldList.add(field);
        }
        ArrayList<ArrayList<String>> data=dataParseService.requestDataToArrayList(req);
        ArrayList<HashMap<String,String>> outData=new ArrayList<HashMap<String, String>>();
        data= IOAdapter.ToLimitedSet(data,fieldList);
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
