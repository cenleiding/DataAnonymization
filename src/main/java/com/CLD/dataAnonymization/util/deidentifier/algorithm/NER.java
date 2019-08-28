package com.CLD.dataAnonymization.util.deidentifier.algorithm;


import com.CLD.dataAnonymization.util.deidentifier.io.FileResolve;
import com.alibaba.fastjson.JSONObject;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/8/21 13:37
 */
public class NER {

    Session tfSession = null;
    HashMap<String,Integer> word2id = null;
    public NER(){
        SavedModelBundle sm = SavedModelBundle.load("./src/main/java/com/CLD/dataAnonymization/util/deidentifier/resources/NER","NER");
        tfSession = sm.session();
        word2id = readWord2id();
    }

    public HashMap<String, HashSet<String>> predict(List<String> context) throws IOException {
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
        int[][] sentences_id = new int[sentences.size()][250];
        int[] lengths = new int[sentences.size()];
        for(int i=0;i<sentences.size();i++){
            String sentence = sentences.get(i);
            int[] id = new int[250];
            int j=0;
            for(j=0;j<250;j++){
                if(j>=sentence.length()) break;
                id[j]=word2id.getOrDefault(String.valueOf(sentence.charAt(j)),word2id.get("UNK"));
            }
            lengths[i] = j;
            sentences_id[i] = id;
        }
        // 预测&提取
        int[][] preResult = predict(sentences_id,lengths);
        HashMap<String,HashSet<String>> outResult = new HashMap<String,HashSet<String>>();
        outResult.put("name",new HashSet<String>());
        outResult.put("address",new HashSet<String>());
        outResult.put("organization",new HashSet<String>());
        outResult.put("detail",new HashSet<String>());
        outResult.put("time",new HashSet<String>());

        for(int i=0;i<sentences.size();i++){
            int[] pre = preResult[i];
            int begin = 0;
            int end = 0;
            while (begin<250){
                while(begin<250 && pre[begin]%2==0) begin++;
                end = begin+1;
                while(end<250 && pre[end]==pre[begin]+1)end++;
                if(begin<250){
                    switch (pre[begin]){
                        case 1 : outResult.get("name").add(sentences.get(i).substring(begin,end)); break;
                        case 3 : outResult.get("address").add(sentences.get(i).substring(begin,end)); break;
                        case 5 : outResult.get("organization").add(sentences.get(i).substring(begin,end)); break;
                        case 7 : outResult.get("detail").add(sentences.get(i).substring(begin,end)); break;
                    }
                }
                begin = end;
            }
        }

        return outResult;
    }

    /**
     * 编码预测
     * @param inputs
     * @param lengths
     * @return
     * @throws IOException
     */
    public int[][] predict(int[][] inputs,int[] lengths) throws IOException {

        int[][] labels = new int[inputs.length][inputs[0].length];
        int[][] result = new int[inputs.length][inputs[0].length];
        Tensor input_x = Tensor.create(inputs);
        Tensor input_l = Tensor.create(lengths);
        Tensor input_b = Tensor.create(labels);
        Tensor<?> out = tfSession.runner()
                .feed("inputs",input_x)
                .feed("lengths",input_l)
                .fetch("predict")
                .run().get(0);
        out.copyTo(result);
        return result;
    }


    /**
     * 读取Word2id 文件
     * @return
     * @throws FileNotFoundException
     */
    public  HashMap<String,Integer> readWord2id() {
        HashMap<String,Integer> word2id = new HashMap<String,Integer>();
        InputStream is=new Object(){
            public InputStream get(){
                return this.getClass().getResourceAsStream("../resources/NER/word2id.json");
            }
        }.get();
        JSONObject jsonObject= null;
        try {
            jsonObject = FileResolve.readerObjectJson(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (String key : jsonObject.keySet()){
            word2id.put(key,Integer.valueOf(jsonObject.getString(key)));
        }
        return word2id;
    }

//    public static void main(String[] args) throws IOException {
//        HashMap<String,Integer> ww = readWord2id();
//
//
//        NER ner = new NER();
//        List<String> context = new ArrayList<String>();
//        context.add("患者姓名史玉腾年龄12岁住院号00148804复印病案用途申请人史玉腾证件及证件号130581200306132219代理人与患者关系证件及证件号出院科室耳鼻咽喉头颈外科出院日期医师签名办理方式邮寄:出院当日办理自取:病案归档后办理办理时间工作日(8:00-11:30,14:30-17:30)办理地点医学影像楼4层病案室重要提示:请携带本申请表患者和代理人有效身份证件原件科室盖章联系电话:14797169824复印时间年月日");
//        context.add("入院记录姓名:赵青爱出生地:山西晋城性别:男职业:退(离)休人员年龄:71岁入院日期:2015年09月民族:汉族记录日期:2015年09月婚否:已婚病历陈述者:患者本人主诉:间断关节肿痛15年,加重2月。");
//        context.add("现病史:2000年06月患者在进食肉类及饮酒后出现左足第1跖趾关节肿痛,就诊于太原市煤炭中心医院,查血尿酸460μmol/L,诊断为\"痛风\",予静滴\"青霉素\"800万Ux5天,口服\"苯溴马隆\"50mg,1次/日治疗,1周后关节肿痛减轻,后因出现腹泻症状更换为\"别嘌醇\"0.1-0.2/日,病情控制良好。");
//        context.add("2003年患者因皮肤红斑就诊于山西医科大学第二医院,诊断为\"变应性血管炎\",考虑为别嘌醇所致,予停用别嘌醇,更换为\"雷公藤多甙\"20mg,3次/日治疗,半年后出现\"心律失常,室性早搏\",遂停用雷公藤多甙。");
//        context.add("2005年冬患者出现右手第2指近端指间关节痛,自行口服中药及\"复方伸筋胶囊\"2粒,2次/日治疗,关节痛减轻。");
//        context.add("2014年09月患者复查血尿酸550μmol/L,自行口服中药及\"碳酸氢钠\"2粒,3次/日治疗,后复查血尿酸降至正常范围,尿PH值升高,停服上述药物。");
//
//        HashMap<String, HashSet<String>> pre = ner.predict(context);
//        System.out.println();
//
//
//    }


}
