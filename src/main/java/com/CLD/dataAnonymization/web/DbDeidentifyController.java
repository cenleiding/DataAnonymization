package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.model.AnonymizeConfigure;
import com.CLD.dataAnonymization.service.deidentifyTarget.dbDeidentify.DbDeidentifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类用于控制器数据库的匿名化操作
 * @Author CLD
 * @Date 2018/5/30 17:02
 **/
@Controller
@RequestMapping("/DbDeidentify")
public class DbDeidentifyController {

    @Autowired
    DbDeidentifyService dbDeidentifyService;


    @RequestMapping("")
    public String DbDeidentify(){
        return "DbDeidentify";
    }

    @RequestMapping(value = "/runDeidentify",method = RequestMethod.POST)
    @ResponseBody
    public List<String> runDeidentify(@RequestParam("dbType") String dbType,
                                      @RequestParam("host") String host,
                                      @RequestParam("port") String port,
                                      @RequestParam("databaseName") String databaseName,
                                      @RequestParam("user") String user,
                                      @RequestParam("password") String password,
                                      @RequestBody AnonymizeConfigure anonymizeConfigure){
        List<String> outlist=new ArrayList<String>();
        dbDeidentifyService.DbDeidentify(dbType,host,port,databaseName,user,password,anonymizeConfigure);
        outlist.add("数据库处理完成！");
        return outlist;
    }

    @RequestMapping(value = "/DbTestConnection",method = RequestMethod.GET)
    @ResponseBody
    public List<String> DbTestConnection(@RequestParam("dbType") String dbType,
                                         @RequestParam("host") String host,
                                         @RequestParam("port") String port,
                                         @RequestParam("databaseName") String databaseName,
                                         @RequestParam("user") String user,
                                         @RequestParam("password") String password){
        String url="";
        Boolean test=false;
        List<String> outlist=new ArrayList<String>();
        test=dbDeidentifyService.testConnect(dbType,host,port,databaseName,user,password);
        if(test==true) outlist.add("数据库连接成功！");
        else outlist.add("数据库连接失败！");
        return outlist;
    }

    @RequestMapping(value = "/getInfo",method = RequestMethod.GET)
    @ResponseBody
    public List<String> getInfo(@RequestParam("dbType") String dbType,
                                @RequestParam("host") String host,
                                @RequestParam("port") String port,
                                @RequestParam("databaseName") String databaseName){
        return dbDeidentifyService.getInfo(dbType,host,port,databaseName);
    }

    @RequestMapping("/getAnonymizeConfigure")
    @ResponseBody
    public AnonymizeConfigure getAnonymizeConfigure(){
        return dbDeidentifyService.getAnonymizeConfigure();
    }

}
