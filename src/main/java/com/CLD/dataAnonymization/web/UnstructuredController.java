package com.CLD.dataAnonymization.web;

import com.CLD.dataAnonymization.util.deidentifier.algorithm.NER;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
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

    @Value("${ner.url}")
    private String ner_url;

    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, HashSet<String>> Unstructured(@RequestBody List<String> context) throws IOException {
        NER ner = new NER();
        return ner.predict(context);
    }


}
