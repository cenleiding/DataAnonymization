package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.deidentifyTarget.apiDeidentify.ApiUsageService;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @Description:该类用于控制显示Api的使用情况
 * @Author CLD
 * @Date 2018/4/16 15:26
 **/
@Controller
@RequestMapping("/ApiUsage")
public class ApiUsageController {

    @Autowired
    ApiUsageService apiUsageService;

    @RequestMapping(value = "/UsageOverView")
    public String UsageOverView(){
        return "UsageOverView";
    }

    @RequestMapping(value = "/UsageDetail")
    public String UsageDetail(){
        return "UsageDetail";
    }

    @RequestMapping(value = "/getUsageOverView")
    @ResponseBody
    public JSONArray getUsageOverView() throws FileNotFoundException {
        return apiUsageService.getUsageOverView();
    }

    @RequestMapping(value = "/getUserIp")
    @ResponseBody
    public Set<String> getUserIp(){
        return apiUsageService.getUserIp();
    }

    @RequestMapping(value = "/getUsageByIp")
    @ResponseBody
    public ArrayList<HashMap<String,String>> getUsageByIp(@RequestParam String Ip){

        ArrayList<HashMap<String, String>> mapArrayList=apiUsageService.getUserDetail(Ip);
        return mapArrayList;
    }


}
