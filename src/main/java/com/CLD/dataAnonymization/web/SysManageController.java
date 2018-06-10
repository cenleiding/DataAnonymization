package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.systemManage.BackUp.FieldFileBackUpService;
import com.CLD.dataAnonymization.service.systemManage.BackUp.NodeFileBackUpService;
import com.CLD.dataAnonymization.service.systemManage.Reset.FieldResetService;
import com.CLD.dataAnonymization.service.systemManage.Reset.NodeResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 该控制器用于管理，用户管理界面
 * @Author CLD
 * @Date 2018/6/5 16:21
 **/
@Controller
public class SysManageController {

    @Autowired
    NodeFileBackUpService nodeFileBackUpService;

    @Autowired
    FieldFileBackUpService fieldFileBackUpService;

    @Autowired
    NodeResetService nodeResetService;

    @Autowired
    FieldResetService fieldResetService;

    @RequestMapping("/SysManage")
    public String sysManage(){
        return "SysManage";
    }

    @RequestMapping(value = "/nodeFileBackUp",method = RequestMethod.GET)
    @ResponseBody
    public Boolean nodeFileBackUp(){
        return nodeFileBackUpService.NodeFileBackUp();
    }

    @RequestMapping(value = "/nodeReset",method = RequestMethod.GET)
    @ResponseBody
    public List<String> nodeReset(){
        return nodeResetService.NodeReset();
    }

    @RequestMapping(value = "/fieldFileBackUp",method = RequestMethod.GET)
    @ResponseBody
    public Boolean fieldFileBackUp(){
        return fieldFileBackUpService.FieldFileBackUp();
    }

    @RequestMapping(value = "/fieldReset",method = RequestMethod.GET)
    @ResponseBody
    public Boolean fieldReset(){
        return fieldResetService.FieldReset();
    }
}
