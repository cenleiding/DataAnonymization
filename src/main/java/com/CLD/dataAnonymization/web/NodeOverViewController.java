package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.OpenEhrNodeClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 该类用于控制展示原型节点的页面
 * @Author CLD
 * @Date 2018/5/22 20:37
 **/
@Controller
public class NodeOverViewController {

    @Autowired
    OpenEhrNodeClassifyService openEhrNodeClassifyService;

    @RequestMapping("/NodeOverView")
    public String nodeOverView(){
        return "NodeOverView";
    }

    @RequestMapping(value = "/getNodeOverView",method = RequestMethod.GET)
    @ResponseBody
    public List<Double> getNodeOverView(){
       return openEhrNodeClassifyService.getNodeOverView();
    }
}
