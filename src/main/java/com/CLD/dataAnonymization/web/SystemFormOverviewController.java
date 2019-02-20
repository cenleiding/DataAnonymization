package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.model.ArchetypeNodeInfo;
import com.CLD.dataAnonymization.model.ExpandNodeInfo;
import com.CLD.dataAnonymization.model.FieldFormInfo;
import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
import com.CLD.dataAnonymization.service.nodeAndField.nodeClassify.ExpandNodeClassifyService;
import com.CLD.dataAnonymization.service.nodeAndField.nodeClassify.OpenEhrNodeClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


/**
 *@Description: 节点表和字段表展示控制器
 *@Author CLD
 *@Date 2019/2/20 15:52
 */
@Controller
public class SystemFormOverviewController {

    @Autowired
    OpenEhrNodeClassifyService openEhrNodeClassifyService;

    @Autowired
    ExpandNodeClassifyService expandNodeClassifyService;

    @Autowired
    FieldClassifyService fieldClassifyService;

    @RequestMapping(value = "/SystemFormOverview")
    public String SystemFormOverview(){return "SystemFormOverview";}

    @RequestMapping(value = "/NodeFormShow/GetArchetypeName", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getArchetypeName(){
        return openEhrNodeClassifyService.getArchetypeName();
    }

    @RequestMapping(value = "/NodeFormShow/GetArchetypeNodeInfoByName",method = RequestMethod.GET)
    @ResponseBody
    public List<ArchetypeNodeInfo> getArchetypeNodeInfoByName(@RequestParam("archetypeName") String archetypeName){
        return openEhrNodeClassifyService.getArchetypeNodeInfoByName(archetypeName);
    }

    @RequestMapping(value = "/NodeFormShow/getExpandFileName",method = RequestMethod.GET)
    @ResponseBody
    public List<String> getFileName(){
        return expandNodeClassifyService.getFileName();
    }

    @RequestMapping(value = "/NodeFormShow/getExpandFromNameByFileName",method = RequestMethod.GET)
    @ResponseBody
    public List<String> getFromNameByFileName(@RequestParam("fileName") String fileName){
        return expandNodeClassifyService.getFromNameByFileName(fileName);
    }

    @RequestMapping(value = "/NodeFormShow/getExpandNodeInfoByName",method = RequestMethod.GET)
    @ResponseBody
    public List<ExpandNodeInfo> getNodeInfoByName(@RequestParam("fileName") String fileName,
                                                  @RequestParam("fromName") String fromName){
        return expandNodeClassifyService.getNodeInfoByName(fileName,fromName);
    }

    @RequestMapping("/FieldFormShow/getFieldFormInfo")
    @ResponseBody
    public List<FieldFormInfo> getFieldFormInfo(){
        return fieldClassifyService.getFieldFormInfo();
    }

    @RequestMapping("/FieldFormShow/getFieldOverViewByFormName")
    @ResponseBody
    public Map<String,Double> getFieldOverViewByFormName(@Param("formName")String formName){
        return fieldClassifyService.getFieldOverViewByFormName(formName);
    }

    @RequestMapping("/FieldFormShow/getFieldDetailByFormName")
    @ResponseBody
    public Map<String,List<String>>getFieldDetailByFormName(@Param("formName")String formName){
        return fieldClassifyService.getFieldDetailByFormName(formName);
    }
}
