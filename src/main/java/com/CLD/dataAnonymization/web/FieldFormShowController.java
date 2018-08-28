package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.model.FieldFormInfo;
import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/15 16:01
 */
@Controller
@RequestMapping("/FieldFormShow")
public class FieldFormShowController {

    @Autowired
    FieldClassifyService fieldClassifyService;

    @RequestMapping("")
    public String FieldFormShow(){
        return "FieldFormShow";
    }

    @RequestMapping("/getFieldFormInfo")
    @ResponseBody
    public List<FieldFormInfo> getFieldFormInfo(){
        return fieldClassifyService.getFieldFormInfo();
    }

    @RequestMapping("/getFieldOverViewByFormName")
    @ResponseBody
    public Map<String,Double> getFieldOverViewByFormName(@Param("formName")String formName){
        return fieldClassifyService.getFieldOverViewByFormName(formName);
    }

    @RequestMapping("/getFieldDetailByFormName")
    @ResponseBody
    public Map<String,List<String>>getFieldDetailByFormName(@Param("formName")String formName){
        return fieldClassifyService.getFieldDetailByFormName(formName);
    }
}
