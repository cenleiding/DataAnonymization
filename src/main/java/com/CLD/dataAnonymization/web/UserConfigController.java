package com.CLD.dataAnonymization.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description: 用户配置界面控制器
 * @Author CLD
 * @Date 2019/2/22 16:10
 */
@Controller
public class UserConfigController {

    @RequestMapping(value = "/UserConfig")
    public String UserConfig(){return "UserConfig";}
}
