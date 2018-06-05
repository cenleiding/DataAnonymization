package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.model.ExpandNodeInfo;
import com.CLD.dataAnonymization.service.nodeAndField.nodeClassify.ExpandNodeClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 该控制器用于控制拓展节点
 * @Author CLD
 * @Date 2018/6/5 10:56
 **/
@Controller
public class ExpandNodeModifyController {

    @Autowired
    ExpandNodeClassifyService expandNodeClassifyService;

    @RequestMapping("/ExpandNodeModify")
    public String expandNodeModify(){
        return "ExpandNodeModify";
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

    @RequestMapping(value = "/updataExpandNodeInfo",method = RequestMethod.POST)
    @ResponseBody
    public List<String> updataNodeInfo(@RequestBody List<ExpandNodeInfo> expandNodeInfoList,
                                       @RequestParam("fileName") String fileName,
                                       @RequestParam("fromName") String fromName){
        return expandNodeClassifyService.updataNodeInfo(expandNodeInfoList,fileName,fromName);
    }
}
