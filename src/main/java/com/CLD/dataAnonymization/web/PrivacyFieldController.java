package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.DataParseService;
import com.CLD.dataAnonymization.service.PrivacyFieldService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 该控制器用于控制隐私字段的查询，修改。
 * @Author CLD
 * @Date 2018/4/2 14:24
 **/
@Controller
@RequestMapping("/PrivacyField")
public class PrivacyFieldController {

    @Autowired
    DataParseService dataParseService;

    @Autowired
    private PrivacyFieldService privacyFieldService;


    @RequestMapping(value = "/PrivacyFieldModify",method = RequestMethod.GET)
    public String viewOfPrivacyFieldModify(){
        return "PrivacyFieldModify";
    }

    @RequestMapping(value = "/PrivacyFieldOverView",method = RequestMethod.GET)
    public String viewOfPrivacyFieldOverView(){
        return "PrivacyFieldOverView";
    }

    /**
     * 提供完整的匿名字段表
     * @param request
     * @return
     * @throws FileNotFoundException
     */
    @RequestMapping(value = "/getPrivacyField",method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getPrivacyField(HttpServletRequest request) throws FileNotFoundException {
        return privacyFieldService.getPrivaryFields();
    }

    /**
     * 提供整合过的匿名字段表
     * @return
     * @throws FileNotFoundException
     */
    @RequestMapping(value = "/getFieldOverView",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getFieldOverView() throws FileNotFoundException {
        return privacyFieldService.getOrganizedFields();
    }

    @RequestMapping(value = "/updataFields",method = RequestMethod.POST)
    @ResponseBody
    public JSONArray updataFields(@RequestBody JSONArray jsonArray) throws FileNotFoundException, UnsupportedEncodingException {
        JSONArray out=new JSONArray();
        ArrayList<String> arrayList=privacyFieldService.checkFields(jsonArray);
        if(arrayList.size()!=0){
            for(String s:arrayList)
                out.add(s+"：数据类型冲突\r\n");
            return out;
        }
        Boolean b=privacyFieldService.updataFieldFile(jsonArray);
        out.add("更新成功");
        return out;
    }
}
