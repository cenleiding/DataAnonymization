package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于处理非结构化数据
 * 自然语言处理 (LSTM+CRF)
 * + 规则匹配
 * + 历史信息
 * @Author CLD
 * @Date 2018/4/12 19:55
 **/
public class Unstructured {


    /**
     * @param data
     * @param col
     * @param proInfo
     * @param dictionary
     * @param regular
     * @return
     */
    public static Boolean unstructuredHandle(ArrayList<ArrayList<String>> data,
                                             ArrayList<Integer> col,
                                             ArrayList<HashMap<String,String>> proInfo,
                                             ArrayList<String> dictionary,
                                             ArrayList<HashMap<String,String>> regular){

        try{
            // 利用机器学习获取隐私信息
            ArrayList<HashMap<String,HashSet<String>>> ner_result = new ArrayList<HashMap<String,HashSet<String>>>();
            // 开启多线程，NER
            ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 1, TimeUnit.DAYS,
                    new LinkedBlockingDeque<Runnable>());
            for (int column :col) {
                int left_index = 0;
                int right_index = 250;
                while(left_index<data.get(0).size()){
                    NerTask nerTask = new NerTask(data.get(column),left_index,Math.min(right_index,data.get(0).size()),ner_result);
                    System.out.println(left_index+":"+right_index+"线程池中线程数目："+executor.getPoolSize()+"，队列中等待执行的任务数目："+
                            executor.getQueue().size()+"，已执行玩别的任务数目："+executor.getCompletedTaskCount());
                    left_index = right_index;
                    right_index+=250;
                }
            }
            executor.shutdown();

            //利用规则获取隐私信息
            ArrayList<HashMap<String,String>> re_result = new ArrayList<HashMap<String,String>>();

            for(int column : col ) {
                re_result.add(unstructured_Re(data.get(column),regular));
            }

            //利用字典获取隐私信息
            ArrayList<HashMap<String,String>> dic_result = new ArrayList<HashMap<String,String>>();

            for(int column : col){
                dic_result.add(unstructured_dic(data.get(column),dictionary));
            }

            // 整合目标字段
            HashMap<String,String> transform = integrate(ner_result,re_result,dic_result,proInfo);

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
        }catch (Exception e){
            return false;
        }
        return true;
    }


    /**
     * NER 任务
     */
    static class NerTask implements Runnable{

        ArrayList<HashMap<String,HashSet<String>>> ner_result;

        List<String> context;

        int left_index;

        int right_index;

        NER ner;

        NerTask(List<String> context,int left_index,int right_index,ArrayList<HashMap<String,HashSet<String>>> ner_result){
            this.context = context;
            this.left_index = left_index;
            this.right_index = right_index;
            this.ner_result = ner_result;
            this.ner = new NER();
        }

        @Override
        public void run() {
            HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
            try {
                result = ner.predict(context.subList(left_index,right_index));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ner_result.add(result);
        }
    }

    /**
     * 使用正则表达式方式识别隐私信息
     * @param context
     * @param regular
     * @return
     */
    public static HashMap<String,String> unstructured_Re(List<String> context,ArrayList<HashMap<String,String>> regular){

        HashMap<String,String> outResult = new HashMap<String,String>();

        for (String text : context){
            for(HashMap<String,String> hashMap : regular){
                Pattern p1 = Pattern.compile(hashMap.get("area"));
                Matcher m1 = p1.matcher(text);
                while(m1.find()){
                    Pattern p2 = Pattern.compile(hashMap.get("aims"));
                    Matcher m2 = p2.matcher(m1.group());
                    m2.find();
                    outResult.put(m1.group(),m1.group().replace(m2.group(),"***"));
                }
            }
        }
        return outResult;
    }


    /**
     * 使用字典获得隐私信息
     * @param context
     * @param dictionary
     * @return
     */
    public static HashMap<String,String> unstructured_dic(List<String> context,ArrayList<String> dictionary){
        HashMap<String,String> outResult = new HashMap<String,String>();
        for (String text : context){
            for(String dic :dictionary)
                if (text.indexOf(dic)!=-1)
                    outResult.put(dic,"***");
        }
        return outResult;
    }


    /**
     * 整合敏感信息
     * @param ner_result
     * @param re_result
     * @param dic_result
     * @param proInfo
     * @return
     */
    public static HashMap<String,String> integrate(ArrayList<HashMap<String,HashSet<String>>> ner_result,
                                                   ArrayList<HashMap<String,String>> re_result,
                                                   ArrayList<HashMap<String,String>> dic_result,
                                                   ArrayList<HashMap<String,String>> proInfo
                                                   )
    {
        HashMap<String,String> out_result = new HashMap<String,String>();

        for(HashMap<String,HashSet<String>> ner : ner_result){
            for(String key:ner.keySet()){
                for(String s:ner.get(key))
                    out_result.put(s,"***");
            }
        }

        for(HashMap<String,String> re : re_result){
            out_result.putAll(re);
        }

        for(HashMap<String,String> dic : dic_result){
            out_result.putAll(dic);
        }

        for(HashMap<String,String> pro : proInfo){
            out_result.putAll(pro);
        }

        return out_result;
    }

//    /**
//     * 使用机器学习方式识别隐私信息
//     * @param context
//     * @return
//     * @throws FileNotFoundException
//     */
//    public static HashMap<String,HashSet<String>> unstructured_NER(List<String> context,String ner_url) {
//        // 断句,去空格 以"。"为标识 ，最长250 context => sentences
//        ArrayList<String> sentences = new ArrayList<String>();
//        for (String text : context){
//            String[] sentence = text.replace(" ","").trim().split("。");
//            for (String s : sentence){
//                int begin = 0;
//                while (begin+250<s.length()){
//                    sentences.add(s.substring(begin,begin+250));
//                    begin+=250;
//                }
//                sentences.add(s.substring(begin,s.length()));
//            }
//        }
//        // word2id sentenses => sentenses_id,lengths
//        // 并补齐 250
//        HashMap<String,Integer> word2id = null;
//        try {
//            word2id = ResourcesReader.readWord2id();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        ArrayList<ArrayList<Integer>> sentences_id = new ArrayList<ArrayList<Integer>>();
//        ArrayList<Integer> lengths = new ArrayList<Integer>();
//        for (String sentence : sentences){
//            ArrayList<Integer> id =  new ArrayList<Integer>();
//            for (char ch: sentence.toCharArray()){
//                if (word2id.keySet().contains(String.valueOf(ch))) id.add(word2id.get(String.valueOf(ch)));
//                else id.add(word2id.get("UNK"));
//            }
//            lengths.add(id.size());
//            for(int i=id.size();i<250;i++)
//                id.add(0);
//            sentences_id.add(id);
//        }
//        /////////////////////   以50组为一个batch发送请求,return => api_result
//        JSONArray api_result = new JSONArray();
//        String s ="{\n" +
//                "\t\"signature_name\":\"predict\",\n" +
//                "\t\"inputs\":{\n" +
//                "\t\t\"input\":[],\n" +
//                "\t\t\"length\":[]\n" +
//                "\t}\n" +
//                "}";
//        JSONObject jsonObject = JSONObject.parseObject(s);
//        int num =0;
//        ArrayList<ArrayList<Integer>> input = new ArrayList<ArrayList<Integer>>();
//        ArrayList<Integer> length = new ArrayList<Integer>();
//        for (int i=0;i<lengths.size();i++){
//            num++;
//            input.add(sentences_id.get(i));
//            length.add(lengths.get(i));
//            if (num>50){
//                jsonObject.getJSONObject("inputs").put("input",input);
//                jsonObject.getJSONObject("inputs").put("length",length);
//                JSONObject re = JSONObject.parseObject(NER_API(jsonObject.toString(),ner_url));
//                api_result.addAll(re.getJSONArray("outputs"));
//                jsonObject = JSONObject.parseObject(s);
//                input.clear();
//                length.clear();
//                num = 0;
//            }
//        }
//        if (!length.isEmpty()){
//            jsonObject.getJSONObject("inputs").put("input",input);
//            jsonObject.getJSONObject("inputs").put("length",length);
//            JSONObject re = JSONObject.parseObject(NER_API(jsonObject.toString(),ner_url));
//            api_result.addAll(re.getJSONArray("outputs"));
//        }
//
//        //////////////// 提取隐私值
//        HashMap<String,HashSet<String>> outResult = new HashMap<String,HashSet<String>>();
//        outResult.put("name",new HashSet<String>());
//        outResult.put("address",new HashSet<String>());
//        outResult.put("organization",new HashSet<String>());
//        outResult.put("detail",new HashSet<String>());
//        outResult.put("time",new HashSet<String>());
//
//        for (int i=0;i<lengths.size();i++){
//            JSONArray label = api_result.getJSONArray(i);
//            ArrayList<Integer> index = new ArrayList<Integer>();
//            for (int j=0;j<label.size();j++)
//                if (label.getInteger(j)%2==1)
//                    index.add(j);
//            char[] sentence = sentences.get(i).toCharArray();
//            for (int j : index){
//                int start = j;
//                int end = j;
//                while((end+1<label.size())&&(label.getInteger(end+1)==label.getInteger(start)+1))
//                    end++;
//                String entity ="";
//                for (int e=start;e<=end;e++)
//                    entity+=sentence[e];
//                if(label.getInteger(start)==1)
//                    outResult.get("name").add(entity.trim().replace(" ",""));
//                if(label.getInteger(start)==3)
//                    outResult.get("address").add(entity.trim().replace(" ",""));
//                if(label.getInteger(start)==5)
//                    outResult.get("organization").add(entity.trim().replace(" ",""));
//                if(label.getInteger(start)==7)
//                    outResult.get("detail").add(entity.trim().replace(" ",""));
//            }
//        }
//        return outResult;
//    }

//    /**
//     * NER_外部API
//     * @param requestJson
//     * @return
//     */
//    public static String NER_API(String requestJson,String ner_url){
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
//        String result = restTemplate.postForObject(ner_url, entity, String.class);
//        return result;
//    }


}
