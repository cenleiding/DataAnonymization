package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.model.FieldFormMap;
import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/15 16:01
 */
@Controller
public class FieldFormShowController {

    @Autowired
    FieldClassifyService fieldClassifyService;

    @RequestMapping("/FieldFormShow")
    public String FieldFormShow(){
        return "FieldFormShow";
    }

    @RequestMapping("/getFromNameMap")
    @ResponseBody
    public List<FieldFormMap> getFromNameMap(){
        return fieldClassifyService.getFromNameMap();
    }
}
