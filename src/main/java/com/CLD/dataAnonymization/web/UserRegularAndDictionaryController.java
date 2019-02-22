package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.dao.h2.entity.RegularLib;
import com.CLD.dataAnonymization.service.regularAndDictionary.regularLib.RegularLibService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Description: 该控制器用于控制用户个人的规则库
 * @Author CLD
 * @Date 2019/2/22 16:01
 */
@Controller
@RequestMapping(value = "/MyReAndDic")
public class UserRegularAndDictionaryController {

    @Autowired
    RegularLibService regularLibService;

    @RequestMapping("/getRegularLibName")
    @ResponseBody
    public List<RegularLib> getRegularLibName(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return regularLibService.getLibByUser(userDetails.getUsername());
    }

}
