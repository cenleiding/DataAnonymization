package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import com.CLD.dataAnonymization.util.deidentifier.resources.ResourcesReader;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于处理非结构化数据
 * @Author CLD
 * @Date 2018/4/12 19:55
 **/
public class Unstructured {

    public static JSONObject unstructured_Api(List<String> context) throws FileNotFoundException {
        // 断句,去空格 以"。"为标识 ，最长250 context => sentences
        ArrayList<String> sentences = new ArrayList<String>();
        for (String text : context){
            String[] sentence = text.replaceAll("/s/n","").split("。");
            for (String s : sentence){
                int begin = 0;
                while (begin+250<s.length()){
                    sentences.add(s.substring(begin,begin+250));
                    begin+=250;
                }
                sentences.add(s.substring(begin,s.length()));
            }
        }
        // word2id sentenses => sentenses_id,lengths
        // 并补齐 250
        HashMap<String,Integer> word2id = ResourcesReader.readWord2id();
        ArrayList<ArrayList<Integer>> sentences_id = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> lengths = new ArrayList<Integer>();
        for (String sentence : sentences){
            ArrayList<Integer> id =  new ArrayList<Integer>();
            for (char ch: sentence.toCharArray()){
                if (word2id.keySet().contains(String.valueOf(ch))) id.add(word2id.get(String.valueOf(ch)));
                else id.add(word2id.get("UNK"));
            }
            lengths.add(id.size());
            for(int i=id.size();i<250;i++)
                id.add(0);
            sentences_id.add(id);
        }
        /////////////////////   以50组为一个batch发送请求,return => api_result
        JSONArray api_result = new JSONArray();
        String s ="{\n" +
                "\t\"signature_name\":\"predict\",\n" +
                "\t\"inputs\":{\n" +
                "\t\t\"input\":[],\n" +
                "\t\t\"length\":[]\n" +
                "\t}\n" +
                "}";
        JSONObject jsonObject = JSONObject.parseObject(s);
        int num =0;
        ArrayList<ArrayList<Integer>> input = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> length = new ArrayList<Integer>();
        for (int i=0;i<lengths.size();i++){
            num++;
            input.add(sentences_id.get(i));
            length.add(lengths.get(i));
            if (num>50){
                jsonObject.getJSONObject("inputs").put("input",input);
                jsonObject.getJSONObject("inputs").put("length",length);
                JSONObject re = JSONObject.parseObject(http_post(jsonObject.toString()));
                api_result.addAll(re.getJSONArray("outputs"));
                jsonObject = JSONObject.parseObject(s);
                input.clear();
                length.clear();
                num = 0;
            }
        }
        if (!length.isEmpty()){
            jsonObject.getJSONObject("inputs").put("input",input);
            jsonObject.getJSONObject("inputs").put("length",length);
            JSONObject re = JSONObject.parseObject(http_post(jsonObject.toString()));
            api_result.addAll(re.getJSONArray("outputs"));
        }

        //////////////// 提取隐私值
        s="{\n" +
            "\t\"NAME\":[],\n" +
            "\t\"ADDRESS\":[],\n" +
            "\t\"ORGANIZATION\":[],\n" +
            "\t\"DETAIL\":[]\n" +
            "}";
        jsonObject = JSONObject.parseObject(s);
        for (int i=0;i<lengths.size();i++){
            JSONArray label = api_result.getJSONArray(i);
            ArrayList<Integer> index = new ArrayList<Integer>();
            for (int j=0;j<label.size();j++)
                if (label.getInteger(j)%2==1)
                    index.add(j);
            char[] sentence = sentences.get(i).toCharArray();
            for (int j : index){
                int start = j;
                int end = j;
                while((end+1<label.size())&&(label.getInteger(end+1)==label.getInteger(start)+1))
                    end++;
                String entity ="";
                for (int e=start;e<=end;e++)
                    entity+=sentence[e];
                if(label.getInteger(start)==1)
                    jsonObject.getJSONArray("NAME").add(entity);
                if(label.getInteger(start)==3)
                    jsonObject.getJSONArray("ADDRESS").add(entity);
                if(label.getInteger(start)==5)
                    jsonObject.getJSONArray("ORGANIZATION").add(entity);
                if(label.getInteger(start)==7)
                    jsonObject.getJSONArray("DETAIL").add(entity);
            }
        }
        System.out.println(jsonObject);
        return jsonObject;
    }

    public static String http_post(String requestJson){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://172.16.119.212:8080/v1/models/EMR_NER:predict";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
        String result = restTemplate.postForObject(url, entity, String.class);
        return result;
    }

    public static Boolean unstructuredHandle(ArrayList<ArrayList<String>> data,
                                             ArrayList<Integer> col,
                                             ArrayList<HashMap<String,String>> proInfo,
                                             HashMap<String,ArrayList<String>> geographic){
        ArrayList<String> geo=new ArrayList<String>();
        geo.addAll(geographic.get("bigCity"));
        geo.addAll(geographic.get("smallCity"));
        for(int column : col ) {
            for(int j=1;j<data.get(column).size();j++) {
                data.get(column).set(j,identity(data.get(column).get(j),proInfo.get(j),geo));
            }
        }
        return true;
    }

    /**
     * @param data           待处理数据
     * @param info           已知可处理字段
     * @param geographic     地理限制范围
     * @return
     */
    public static String identity(String data, HashMap<String,String> info,ArrayList<String>geographic){
        Pattern p=null;
        Matcher m=null;

        //姓名
        p=Pattern.compile("姓\\s*名\\s*[：|:]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "姓    名: ***");
        }

        //时间
        p=Pattern.compile("\\d{4}\\s*[-|年|/]\\s*\\d{1,2}\\s*[-|月|/]\\s*\\d{1,2}\\s*日?");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\d{4}\\s*[-|年|/]\\s*\\d{1,2}\\s*[-|月|/]");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            data=data.replace(m.group(), m1.group());
        }
        //年龄
        p=Pattern.compile("年\\s*龄\\s*[：|:]\\s*\\d*\\s*岁");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\d+");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            if(Integer.parseInt(m1.group())>90) data=data.replace(m.group(), "年 龄：大于90岁");
        }
        //住院号
        p=Pattern.compile("住\\s*院\\s*号\\s*[:|：]\\s*\\d+");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\d+");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "住 院 号: *****");
        }
        //医师签名
        p=Pattern.compile("医\\s*师\\s*签\\s*名\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "医 师 签 名: ***");
        }
        //主治医师
        p=Pattern.compile("主\\s*治\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "主 治 医 师: ***");
        }
        //住院医师
        p=Pattern.compile("住\\s*院\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "住 院 医 师: ***");
        }
        //责任护师
        p=Pattern.compile("责\\s*任\\s*护\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "责 任 护 师: ***");
        }
        //上级医师
        p=Pattern.compile("上\\s*级\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "上 级 医 师: ***");
        }
        //主管医师
        p=Pattern.compile("主\\s*管\\s*医\\s*师\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            info.put(m1.group().replace(" ", ""),"***");
            data=data.replace(m.group(), "主 管 医 师: ***");
        }
        //病房
        p=Pattern.compile("病\\s*房\\s*[:|：]\\S*");
        m=p.matcher(data);
        while(m.find()){
            data=data.replace(m.group(), "病 房: ***");
        }
        //床号
        p=Pattern.compile("床\\s*号\\s*[:|：]\\S*");
        m=p.matcher(data);
        while(m.find()){
            data=data.replace(m.group(), "床 号: ***");
        }
        //出生地
        p=Pattern.compile("出\\s*生\\s*地\\s*[:|：]\\s*[\\u4e00-\\u9fa5]*");
        m=p.matcher(data);
        while(m.find()){
            Pattern p1=Pattern.compile("\\s*[\\u4e00-\\u9fa5]*$");
            Matcher m1=p1.matcher(m.group());
            m1.find();
            String value="";
            for(int k=0;k<geographic.size();k++){
                if(m1.group().indexOf(geographic.get(k))!=-1)
                    value+=geographic.get(k);
            }
            data=data.replace(m.group(),  "出 生 地 ："+value);
        }

        //移除历史信息
        for (String inf:info.keySet()) {
            if (!inf.replace(" ","").equals(""))
            data=data.replaceAll(inf,info.get(inf));
        }
        return data;
    }

}
