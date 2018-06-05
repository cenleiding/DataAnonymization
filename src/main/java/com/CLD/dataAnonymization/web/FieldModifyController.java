package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.model.FieldInfo;
import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 该控制器用于控制字段的修改，新建，删除
 * @Author CLD
 * @Date 2018/5/23 9:28
 **/
@Controller
public class FieldModifyController {

    @Autowired
    FieldClassifyService fieldClassifyService;

    @RequestMapping("/FieldModify")
    public String fieldMOdify(){
        return "FieldModify";
    }

    @RequestMapping(value = "/getFromNameList",method = RequestMethod.GET)
    @ResponseBody
    public List<String> getFromNameList(){
        return fieldClassifyService.getFromNameList();
    };

    @RequestMapping(value = "/getFieldByFromName",method = RequestMethod.GET)
    @ResponseBody
    public List<FieldInfo> getFieldByFromName(@Param("fromName") String fromName){
        return fieldClassifyService.getFieldByFromName(fromName);
    };

    @RequestMapping(value = "/updataField",method = RequestMethod.POST)
    @ResponseBody
    public List<String> updataField(@RequestBody List<FieldInfo> fieldInfoList,@Param("newFromName") String newFromName){
        return fieldClassifyService.updataField(fieldInfoList,newFromName);
    };

    @RequestMapping(value = "/deleteFromByName",method = RequestMethod.GET)
    @ResponseBody
    public Boolean deleteFromByName(@Param("fromName")String fromName){
        fieldClassifyService.deleteFromByName(fromName);
        return true;
    }
}
