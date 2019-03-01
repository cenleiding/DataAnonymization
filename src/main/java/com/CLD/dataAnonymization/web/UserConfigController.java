package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.regularAndDictionary.regularLib.RegularLibService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description: 用户配置界面控制器
 * @Author CLD
 * @Date 2019/2/22 16:10
 */
@Controller
@RequestMapping(value = "/UserConfig")
public class UserConfigController {

    @Autowired
    RegularLibService regularLibService;

    @RequestMapping(value = "")
    public String UserConfig(){return "UserConfig";}




}
