package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.regularAndDictionary.regularLib.RegularLibService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

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


    @RequestMapping(value = "/SaveChange")
    @ResponseBody
    public List<String> SaveChange(@Param("old_libName") String old_libName,
                           @Param("new_libName") String new_libName,
                           @Param("new_description") String new_description){
        List<String> response = new ArrayList<String>();
        response.add(regularLibService.updataLib(old_libName,new_libName,new_description));
        return response;
    }

}
