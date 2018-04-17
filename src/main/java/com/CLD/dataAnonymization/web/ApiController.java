package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.ApiUsageService;
import com.CLD.dataAnonymization.service.DataParseService;
import com.CLD.dataAnonymization.service.PrivacyFieldService;
import com.CLD.dataAnonymization.util.deidentifier.IOConfiguration;
import com.CLD.dataAnonymization.util.deidentifier.SafeHarbor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.sun.xml.internal.stream.writers.XMLStreamWriterImpl.UTF_8;

/**
 * 该控制器提供数据匿名化接口
 * @Author CLD
 * @Date 2018/3/29 10:35
 **/
@RestController
public class ApiController {

    @Autowired
    DataParseService dataParseService;

    @Autowired
    PrivacyFieldService privacyFieldService;

    @Autowired
    ApiUsageService apiUsageService;

    final static String ErrorDataSource="[{\"错误\":\"数据格式错误\"}]";

    @RequestMapping(value = "/SafeHarbor",method = RequestMethod.POST)
    @ResponseBody
    public JSONArray SafeHarbor(HttpServletRequest req) throws IOException {
        ArrayList<ArrayList<String>> list=dataParseService.requestDataToArrayList(req);
        if(list==null)
            return JSON.parseArray(ErrorDataSource);

        list=IOConfiguration.ToSafeHarbor(list,privacyFieldService.getProcessingFields());
        JSONArray jsonArray=dataParseService.ArrayListToJsonArray(list);
        apiUsageService.addUsageLog(req.getRemoteAddr(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                list.get(0),String.valueOf(list.size()-1),"SafeHarbor");
        return jsonArray;
    }

    @RequestMapping(value = "/LimitedSet",method = RequestMethod.POST)
    @ResponseBody
    public JSONArray LimitedSet(HttpServletRequest req) throws IOException {
        ArrayList<ArrayList<String>> list=dataParseService.requestDataToArrayList(req);
        if(list==null)
            return JSON.parseArray(ErrorDataSource);

        list=IOConfiguration.ToLimitedSet(list,privacyFieldService.getProcessingFields());
        JSONArray jsonArray=dataParseService.ArrayListToJsonArray(list);
        apiUsageService.addUsageLog(req.getRemoteAddr(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                list.get(0),String.valueOf(list.size()-1),"LimitedSet");
        return jsonArray;
    }

}
