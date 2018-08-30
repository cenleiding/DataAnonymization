package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.model.ArchetypeNodeInfo;
import com.CLD.dataAnonymization.model.ExpandNodeInfo;
import com.CLD.dataAnonymization.service.nodeAndField.nodeClassify.ExpandNodeClassifyService;
import com.CLD.dataAnonymization.service.nodeAndField.nodeClassify.OpenEhrNodeClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @description: 节点表展示控件
 * @Author CLD
 * @Date 2018/8/23 12:16
 */
@Controller
@RequestMapping("/NodeFormShow")
public class NodeFormShowController {

    @Autowired
    OpenEhrNodeClassifyService openEhrNodeClassifyService;

    @Autowired
    ExpandNodeClassifyService expandNodeClassifyService;

    @RequestMapping("")
    public String NodeFormShow(){
        return "NodeFormShow";
    }

    @RequestMapping(value = "/GetArchetypeName", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getArchetypeName(){
        return openEhrNodeClassifyService.getArchetypeName();
    }

    @RequestMapping(value = "/GetArchetypeNodeInfoByName",method = RequestMethod.GET)
    @ResponseBody
    public List<ArchetypeNodeInfo> getArchetypeNodeInfoByName(@RequestParam("archetypeName") String archetypeName){
        return openEhrNodeClassifyService.getArchetypeNodeInfoByName(archetypeName);
    }

    @RequestMapping(value = "/getExpandFileName",method = RequestMethod.GET)
    @ResponseBody
    public List<String> getFileName(){
        return expandNodeClassifyService.getFileName();
    }

    @RequestMapping(value = "/getExpandFromNameByFileName",method = RequestMethod.GET)
    @ResponseBody
    public List<String> getFromNameByFileName(@RequestParam("fileName") String fileName){
        return expandNodeClassifyService.getFromNameByFileName(fileName);
    }

    @RequestMapping(value = "/getExpandNodeInfoByName",method = RequestMethod.GET)
    @ResponseBody
    public List<ExpandNodeInfo> getNodeInfoByName(@RequestParam("fileName") String fileName,
                                                  @RequestParam("fromName") String fromName){
        return expandNodeClassifyService.getNodeInfoByName(fileName,fromName);
    }

}
