package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.service.systemManage.webSecurity.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/10 12:34
 */
@Controller
public class LoginController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public HashMap getUser(){
        HashMap<String,String> user=new HashMap<String,String>();
        user.put("user","未登入");
        user.put("flag","0");
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            user.put("user",userDetails.getUsername());
            user.put("flag","1");
        }catch (Exception e){}
        return user;
    }

    @RequestMapping("/register")
    @ResponseBody
    public HashMap register(@Param("username")String username,@Param("password") String password){
        HashMap<String,String> flag=new HashMap<String,String>();
        if(userServiceImpl.addUser(username,password)){
            flag.put("flag","1");
        }else {
            flag.put("flag","0");
        }
        return flag;
    }
}
