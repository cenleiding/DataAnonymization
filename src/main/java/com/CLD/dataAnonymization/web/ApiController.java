package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.ApiDeidentifyService;
import com.CLD.dataAnonymization.service.ApiUsageService;
import com.CLD.dataAnonymization.service.FieldClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * 该控制器提供数据匿名化接口
 * @Author CLD
 * @Date 2018/3/29 10:35
 **/
@RestController
public class ApiController {

    @Autowired
    ApiDeidentifyService apiDeidentifyService;

    @Autowired
    ApiUsageService apiUsageService;

    @Autowired
    FieldClassifyService fieldClassifyService;

    final static String ErrorDataSource="[{\"错误\":\"数据格式错误\"}]";

    @RequestMapping(value = "/SafeHarbor",method = RequestMethod.POST)
    @ResponseBody
    public ArrayList<HashMap<String,String>> SafeHarbor(HttpServletRequest req,
                                                   @RequestParam(value = "fieldFromName",defaultValue = "Original",required = false) String fieldFromName) throws IOException {
        ArrayList<HashMap<String,String>> data=apiDeidentifyService.ApiSafeHarbor(req,fieldFromName);
        apiUsageService.addUsageLog(req.getRemoteAddr(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                new ArrayList<String>(data.get(0).keySet()),String.valueOf(data.size()-1),"SafeHarbor");
        return data;
    }

    @RequestMapping(value = "/LimitedSet",method = RequestMethod.POST)
    @ResponseBody
    public ArrayList<HashMap<String,String>> LimitedSet(HttpServletRequest req,
                                                   @RequestParam(value = "fieldFromName",defaultValue = "Original",required = false) String fieldFromName) throws IOException {
        ArrayList<HashMap<String,String>> data=apiDeidentifyService.ApiLimitedSet(req,fieldFromName);
        apiUsageService.addUsageLog(req.getRemoteAddr(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                new ArrayList<String>(data.get(0).keySet()),String.valueOf(data.size()-1),"LimitedSet");
        return data;
    }

}
