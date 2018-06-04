package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.model.ArchetypeNodeInfo;
import com.CLD.dataAnonymization.service.OpenEhrNodeClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 该控制器用于控制原型节点
 * @Author CLD
 * @Date 2018/5/22 16:16
 **/
@Controller
public class NodeModifyController {

    @Autowired
    OpenEhrNodeClassifyService openEhrNodeClassifyService;

    @RequestMapping("/NodeModify")
    public String nodeModify(){
        return "NodeModify";
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

    @RequestMapping(value = "/UpdataNodeInfo",method = RequestMethod.POST)
    @ResponseBody
    public List<String> updataNodeInfo(@RequestBody List<ArchetypeNodeInfo> archetypeNodeInfoList, @RequestParam("archetypeNmae") String archetypeName){
        return openEhrNodeClassifyService.updataNodeInfo(archetypeNodeInfoList,archetypeName);
    }
}
