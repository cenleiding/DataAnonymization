package com.CLD.dataAnonymization.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * 该类用于对隐私字段的获取，展示和修改
 * @Author CLD
 * @Date 2018/4/2 14:00
 **/
@Service
public class PrivacyFieldServiceImpl implements PrivacyFieldService {

    @Override
    public JSONArray getPrivaryFields() throws FileNotFoundException {

        JSONArray outJson=new JSONArray();
        String URL =this.getClass().getResource("/").getPath()+"/com/CLD/dataAnonymization/util/deidentifier/Fields/Form_mapping.json";
        JSONReader reader=new JSONReader(new FileReader(URL));
        reader.startArray();
        while(reader.hasNext()) {
            JSONObject ja= (JSONObject) reader.readObject();
            outJson.add(ja);
        }
        reader.endArray();
        reader.close();
        return outJson;
    };
}
