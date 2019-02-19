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
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.CLD.dataAnonymization.util.deidentifier.resources.ResourcesReader.readRegular;

/**
 * 用于处理非结构化数据
 * 自然语言处理 (LSTM+CRF)
 * + 规则匹配
 * + 历史信息
 * @Author CLD
 * @Date 2018/4/12 19:55
 **/
public class Unstructured {



    public static Boolean unstructuredHandle(ArrayList<ArrayList<String>> data,
                                             ArrayList<Integer> col,
                                             ArrayList<HashMap<String,String>> proInfo,
                                             HashMap<String,ArrayList<String>> geographic){
        ArrayList<String> geo=new ArrayList<String>();
        geo.addAll(geographic.get("bigCity"));
        geo.addAll(geographic.get("smallCity"));

        HashMap<String,HashSet<String>> labels = new HashMap<String,HashSet<String>>();
        labels.put("name",new HashSet<String>());
        labels.put("address",new HashSet<String>());
        labels.put("organization",new HashSet<String>());
        labels.put("detail",new HashSet<String>());
        labels.put("time",new HashSet<String>());


        // 利用机器学习获取隐私信息
        try {
            for (int column :col) {
                HashMap<String,HashSet<String>> ner_result = unstructured_NER(data.get(column));
                for (String s:labels.keySet()){
                    labels.get(s).addAll(ner_result.get(s));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //利用规则获取隐私信息
        try {
            for(int column : col ) {
                HashMap<String, HashSet<String>> re_result = unstructured_Re(data.get(column));
                for (String s:labels.keySet()){
                    labels.get(s).addAll(re_result.get(s));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 整合目标字段
        HashMap<String,String> transform = integrate(labels,proInfo,geo);

        // 替换敏感字符
        for (int column : col){
            for (int i = 0;i<data.get(column).size();i++){
                String text = data.get(column).get(i);
                for(String key:transform.keySet()){
                    text = text.replaceAll(key,transform.get(key));
                }
                data.get(column).set(i,text);
            }
        }

        return true;
    }

    /**
     * 使用机器学习方式识别隐私信息
     * @param context
     * @return
     * @throws FileNotFoundException
     */
    public static HashMap<String,HashSet<String>> unstructured_NER(List<String> context) throws FileNotFoundException {
        // 断句,去空格 以"。"为标识 ，最长250 context => sentences
        ArrayList<String> sentences = new ArrayList<String>();
        for (String text : context){
            String[] sentence = text.replace(" ","").trim().split("。");
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
                JSONObject re = JSONObject.parseObject(NER_API(jsonObject.toString()));
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
            JSONObject re = JSONObject.parseObject(NER_API(jsonObject.toString()));
            api_result.addAll(re.getJSONArray("outputs"));
        }

        //////////////// 提取隐私值
        HashMap<String,HashSet<String>> outResult = new HashMap<String,HashSet<String>>();
        outResult.put("name",new HashSet<String>());
        outResult.put("address",new HashSet<String>());
        outResult.put("organization",new HashSet<String>());
        outResult.put("detail",new HashSet<String>());
        outResult.put("time",new HashSet<String>());

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
                    outResult.get("name").add(entity.trim().replace(" ",""));
                if(label.getInteger(start)==3)
                    outResult.get("address").add(entity.trim().replace(" ",""));
                if(label.getInteger(start)==5)
                    outResult.get("organization").add(entity.trim().replace(" ",""));
                if(label.getInteger(start)==7)
                    outResult.get("detail").add(entity.trim().replace(" ",""));
            }
        }
        return outResult;
    }

    /**
     * 使用正则表达式方式识别隐私信息
     * @param context
     * @return
     */
    public static HashMap<String,HashSet<String>> unstructured_Re(List<String> context) throws FileNotFoundException {
        HashMap<String,ArrayList<String>> regular = readRegular();

        HashMap<String,HashSet<String>> outResult = new HashMap<String,HashSet<String>>();
        outResult.put("name",new HashSet<String>());
        outResult.put("address",new HashSet<String>());
        outResult.put("organization",new HashSet<String>());
        outResult.put("detail",new HashSet<String>());
        outResult.put("time",new HashSet<String>());
        Pattern p=null;
        Matcher m=null;
        for (String text : context){
            for (String s : outResult.keySet()){
                for(int i=0;i<regular.get(s).size();i=i+2){
                    p=Pattern.compile(regular.get(s).get(i));
                    m=p.matcher(text);
                    while(m.find()){
                        Pattern p1=Pattern.compile(regular.get(s).get(i+1));
                        Matcher m1=p1.matcher(m.group());
                        m1.find();
                        outResult.get(s).add(m1.group().trim().replace(" ",""));
                    }
                }
            }
        }
        return outResult;
    }

    /**
     * 整合敏感信息
     * @param labels
     * @param proInfo
     * @return
     */
    public static HashMap<String,String> integrate(HashMap<String,HashSet<String>> labels,
                                                   ArrayList<HashMap<String,String>> proInfo,
                                                   ArrayList<String> geographic)
    {
        HashMap<String,String> out_result = new HashMap<String,String>();
        HashSet<String> flag = new HashSet<String>();

        // 处理历史信息
        for (int i=0;i<proInfo.size();i++){
            for (String key:proInfo.get(i).keySet()){
                if (flag.contains(key)) continue;
                flag.add(key);
                out_result.put(key,proInfo.get(i).get(key));
            }
        }

        // 只处理长度大于1的姓名
        for (String s :labels.get("name")){
            if ((s.length()<2)||(flag.contains(s))) continue;
            flag.add(s);
            out_result.put(s,"***");
        }

        // 只处理长度大于1的地理位置
        // 且与地理表进行对比
        for (String s :labels.get("address")){
            if ((s.length()<2)||(flag.contains(s))) continue;
            flag.add(s);
            String value="";
            for(String g:geographic){
                if(s.indexOf(g)!=-1)
                    value+=g;
            }
            out_result.put(s,value);
        }

        // 只处理长度大于1的组织信息
        for (String s :labels.get("organization")){
            if ((s.length()<2)||(flag.contains(s))) continue;
            flag.add(s);
            out_result.put(s,"***");
        }

        // 只处理长度大于1的细节信息
        for (String s :labels.get("detail")){
            if ((s.length()<2)||(flag.contains(s))) continue;
            flag.add(s);
            out_result.put(s,"***");
        }

        // 只处理长度大于1的time信息
        // 并去除日期信息
        for (String s :labels.get("time")){
            if ((s.length()<2)||(flag.contains(s))) continue;
            flag.add(s);
            int temp = Math.max(s.lastIndexOf("/"),s.lastIndexOf("月")+1);
            int index = Math.max(temp,s.lastIndexOf("-"));
            out_result.put(s,s.substring(0,index));
        }



        return out_result;
    }


    /**
     * NER_外部API
     * @param requestJson
     * @return
     */
    public static String NER_API(String requestJson){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://172.16.119.212:8080/v1/models/EMR_NER:predict";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
        String result = restTemplate.postForObject(url, entity, String.class);
        return result;
    }


}
