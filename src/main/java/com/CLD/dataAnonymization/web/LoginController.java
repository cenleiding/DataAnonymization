package com.CLD.dataAnonymization.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/10 12:34
 */
@Controller
public class LoginController {

    @RequestMapping("/login")
    public String login(){
        return "login";
    }
}
