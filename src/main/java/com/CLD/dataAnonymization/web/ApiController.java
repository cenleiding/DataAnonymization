package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.model.AnonymizeConfigure;
import com.CLD.dataAnonymization.service.deidentifyTarget.apiDeidentify.ApiDeidentifyService;
import com.CLD.dataAnonymization.service.deidentifyTarget.apiDeidentify.ApiUsageService;
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
@RequestMapping("/dataDeidentify")
public class ApiController {

    @Autowired
    ApiDeidentifyService apiDeidentifyService;

    @Autowired
    ApiUsageService apiUsageService;

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    public ArrayList<HashMap<String,String>> dataDeidentify(HttpServletRequest req,
                                                            @RequestParam(value = "fieldFromName",defaultValue = "OpenEhr字段表",required = false) String fieldFromName,
                                                            @RequestParam(value = "level",defaultValue = "Level2",required = false) String level,
                                                            @RequestParam(value = "encryptPassword",defaultValue = "CLD",required = false) String encryptPassword,
                                                            @RequestParam(value = "noiseScope_big",defaultValue = "0.08",required = false) String noiseScope_big,
                                                            @RequestParam(value = "noiseScope_small",defaultValue = "0.05",required = false) String noiseScope_small,
                                                            @RequestParam(value = "k_big",defaultValue = "5",required = false) String k_big,
                                                            @RequestParam(value = "k_small",defaultValue = "3",required = false) String k_small,
                                                            @RequestParam(value = "t",defaultValue = "0.2",required = false) String t,
                                                            @RequestParam(value = "suppressionLimit_level1",defaultValue = "0.9",required = false) String suppressionLimit_level1,
                                                            @RequestParam(value = "suppressionLimit_level2",defaultValue = "0.9",required = false) String suppressionLimit_level2,
                                                            @RequestParam(value = "microaggregation",defaultValue = "10",required = false) String microaggregation) throws IOException {
        AnonymizeConfigure anonymizeConfigure=new AnonymizeConfigure();
        anonymizeConfigure.setFieldFormName(fieldFromName);
        anonymizeConfigure.setT(t);
        anonymizeConfigure.setSuppressionLimit_level2(suppressionLimit_level2);
        anonymizeConfigure.setSuppressionLimit_level1(suppressionLimit_level1);
        anonymizeConfigure.setNoiseScope_small(noiseScope_small);
        anonymizeConfigure.setNoiseScope_big(noiseScope_big);
        anonymizeConfigure.setMicroaggregation(microaggregation);
        anonymizeConfigure.setK_small(k_small);
        anonymizeConfigure.setK_big(k_big);
        anonymizeConfigure.setEncryptPassword(encryptPassword);
        anonymizeConfigure.setLevel(level);
        ArrayList<HashMap<String,String>> data=apiDeidentifyService.ApiDataDeidentify(req,anonymizeConfigure);
        apiUsageService.addUsageLog(req.getRemoteAddr(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                new ArrayList<String>(data.get(0).keySet()),String.valueOf(data.size()-1),"LimitedSet");
        return data;
    }

}
