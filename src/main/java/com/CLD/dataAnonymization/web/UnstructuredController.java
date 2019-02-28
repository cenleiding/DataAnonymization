package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.util.deidentifier.algorithm.Unstructured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.HashSet;
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
    public HashMap<String, HashSet<String>> Unstructured(@RequestBody List<String> context) {
        return Unstructured.unstructured_NER(context);
    }


}
