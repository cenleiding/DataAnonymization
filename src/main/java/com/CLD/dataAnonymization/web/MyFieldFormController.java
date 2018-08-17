package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/17 16:04
 */
@Controller
public class MyFieldFormController {

    @Autowired
    FieldClassifyListRepository fieldClassifyListRepository;

    @RequestMapping("/MyFieldForm")
    public String myFieldForm(){
        return "MyFieldForm";
    }


    @RequestMapping("/getFieldFormName")
    @ResponseBody
    public List<String> getFieldFormName(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return fieldClassifyListRepository.getFormNameByUserName(userDetails.getUsername());
    }

}
