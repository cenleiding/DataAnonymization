package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.util.deidentifier.algorithm.Unstructured;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * @Description: 非结构化文本处理接口
 * @Author CLD
 * @Date 2018/11/22 12:36
 */
@Controller
@RequestMapping("/Unstructured")
public class UnstructuredController {

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject Unstructured(@RequestBody List<String> context){
        try {
            return Unstructured.unstructured_Api(context);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;}

}
