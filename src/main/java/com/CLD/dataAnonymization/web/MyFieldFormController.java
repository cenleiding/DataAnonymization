package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.dao.h2.entity.FieldChangeLog;
import com.CLD.dataAnonymization.dao.h2.repository.FieldChangeLogRepository;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyListRepository;
import com.CLD.dataAnonymization.model.FieldFormInfo;
import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/17 16:04
 */
@Controller
@RequestMapping("/MyFieldForm")
public class MyFieldFormController {

    @Autowired
    FieldClassifyListRepository fieldClassifyListRepository;

    @Autowired
    FieldClassifyService fieldClassifyService;

    @Autowired
    FieldChangeLogRepository fieldChangeLogRepository;

    @RequestMapping("/MyFieldForm")
    public String myFieldForm(){
        return "MyFieldForm";
    }


    @RequestMapping("/getFieldFormName")
    @ResponseBody
    public List<String> getFieldFormName(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return fieldClassifyListRepository.getFormNameByUserName(userDetails.getUsername());
    }

    @RequestMapping("/getFieldFormInfoByFormName")
    @ResponseBody
    public FieldFormInfo getFieldFormInfoByFormName(@Param("formName") String formName){
        FieldFormInfo fieldFormInfo=fieldClassifyService.getFieldFormInfoByFormName(formName);
        return fieldFormInfo;
    }


    @RequestMapping("/getFieldDetailByFormName")
    @ResponseBody
    public Map<String,List<String>> getFieldDetailByFormName(@Param("formName")String formName){
        return fieldClassifyService.getFieldDetailByFormName(formName);
    }

    @RequestMapping("/getFieldOverViewByFormName")
    @ResponseBody
    public Map<String,Double> getFieldOverViewByFormName(@Param("formName")String formName){
        return fieldClassifyService.getFieldOverViewByFormName(formName);
    }
    @RequestMapping("/getFieldChangeLogByFormName")
    @ResponseBody
     public List<FieldChangeLog> getFieldChangeLogByFormName(@Param("formName")String formName){
         return fieldChangeLogRepository.findByFormName(formName);
     }

}
