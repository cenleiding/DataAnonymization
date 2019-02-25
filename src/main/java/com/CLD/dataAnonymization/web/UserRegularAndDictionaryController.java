package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.dao.h2.entity.Dictionary;
import com.CLD.dataAnonymization.dao.h2.entity.RegularLib;
import com.CLD.dataAnonymization.service.regularAndDictionary.dictionary.DictionaryService;
import com.CLD.dataAnonymization.service.regularAndDictionary.regularLib.RegularLibService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

    @Autowired
    DictionaryService dictionaryService;

    @RequestMapping("/getRegularLibName")
    @ResponseBody
    public List<RegularLib> getRegularLibName(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return regularLibService.getLibByUser(userDetails.getUsername());
    }

    @RequestMapping("/getDictionaryByLibName")
    @ResponseBody
    public List<Dictionary> getDictionaryByLibName(@Param("libName") String libName){
        return dictionaryService.getDictionaryByLibName(libName);
    }

    @RequestMapping(value = "/uploadDictionary",method = RequestMethod.POST)
    @ResponseBody
    public List<String> uploadDictionary(MultipartHttpServletRequest re) throws IOException {
        List<String> response = new ArrayList<String>();
        response.add(dictionaryService.uploadDictionaryFile(re));
        return response;
    }

    @RequestMapping(value = "/deleteDictionary")
    @ResponseBody
    public Boolean deleteDictionary(@Param("libName") String libName,@Param("fileName") String fileName){
        dictionaryService.deleteDictionary(fileName,libName);
        return true;
    }

    @RequestMapping(value = "/downloadDictionary")
    @ResponseBody
    public List<String> downloadDictionary(HttpServletRequest rq,@Param("libName") String libName, @Param("fileName") String fileName) throws UnsupportedEncodingException {
        List<String> respone = new ArrayList<String>();
        String url=dictionaryService.downloadDictionary(rq,fileName,libName);
        respone.add(url);
        return respone;
    }

}
