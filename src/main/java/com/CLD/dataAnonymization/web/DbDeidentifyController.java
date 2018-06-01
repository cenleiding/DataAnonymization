package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.DbDeidentifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类用于控制器数据库的匿名化操作
 * @Author CLD
 * @Date 2018/5/30 17:02
 **/
@Controller
public class DbDeidentifyController {

    @Autowired
    DbDeidentifyService dbDeidentifyService;


    @RequestMapping("/DbDeidentify")
    public String DbDeidentify(){
        return "DbDeidentify";
    }

    @RequestMapping(value = "/runDeidentify",method = RequestMethod.GET)
    @ResponseBody
    public List<String> runDeidentify(@RequestParam("dbType") String dbType,
                                      @RequestParam("host") String host,
                                      @RequestParam("port") String port,
                                      @RequestParam("databaseName") String databaseName,
                                      @RequestParam("user") String user,
                                      @RequestParam("password") String password,
                                      @RequestParam("method") String method,
                                      @RequestParam("fieldFromName") String fieldFromName){
        List<String> outlist=new ArrayList<String>();
        dbDeidentifyService.DbDeidentify(dbType,host,port,databaseName,user,password,method,fieldFromName);
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
        System.out.println("开始查询");
        return dbDeidentifyService.getInfo(dbType,host,port,databaseName);
    }



}
