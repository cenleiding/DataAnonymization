package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.PrivacyFieldService;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;

/**
 * 该控制器用于控制隐私字段的查询，修改。
 * @Author CLD
 * @Date 2018/4/2 14:24
 **/
@Controller
@RequestMapping("/PrivacyField")
public class PrivacyFieldController {

    @Autowired
    private PrivacyFieldService privacyFieldService;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String viewOfPrivacyField(){
        return "PrivacyField";
    }

    @RequestMapping(value = "/getPrivacyField",method = RequestMethod.GET)
    @ResponseBody
    public JSONArray getPrivacyField(HttpServletRequest request) throws FileNotFoundException {
        JSONArray jsonArray=privacyFieldService.getPrivaryFields();
        return jsonArray;
    }
}
