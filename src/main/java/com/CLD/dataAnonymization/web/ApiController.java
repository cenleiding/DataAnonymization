package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.DataParseService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;

import static com.sun.xml.internal.stream.writers.XMLStreamWriterImpl.UTF_8;

/**
 * @Author CLD
 * @Date 2018/3/29 10:35
 **/
@RestController
public class ApiController {

    @Autowired
    DataParseService dataParseService;

    final static String ErrorDataSource="[{\"错误\":\"数据格式错误\"}]";

    @RequestMapping(value = "/SafeHarbor",method = RequestMethod.POST)
    public JSONArray SafeHarbor(HttpServletRequest req) throws IOException {
        ArrayList<ArrayList<String>> list=dataParseService.requestDataToArrayList(req);
        if(list==null)
            return JSON.parseArray(ErrorDataSource);
        JSONArray jsonArray=dataParseService.ArrayListToJsonArray(list);
        return jsonArray;
    }

}
