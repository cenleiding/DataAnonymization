package com.CLD.dataAnonymization.util.deidentifier.algorithm;


import com.CLD.dataAnonymization.util.deidentifier.resources.ResourcesReader;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.FileNotFoundException;
import java.io.IOException;
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
    SavedModelBundle sm = null;
    public NER(){
        sm = SavedModelBundle.load("./src/main/java/com/CLD/dataAnonymization/util/deidentifier/resources/NER","NER");
        tfSession = sm.session();
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
        HashMap<String,Integer> word2id = null;
        try {
            word2id = ResourcesReader.readWord2id();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args) throws IOException {
        NER ner = new NER();
        List<String> context = new ArrayList<String>();
        context.add("会诊记录(一般)住院号:kLrV0y姓名:彭爱竹性别:女年龄:81岁病房:kLrV0y床号:kLrV0y邀请耳鼻喉科会诊申请日期:2016-01-12:14病历摘要:会诊目的:请求科室:医师签名:彭爱竹接到申请会诊时间:年月日时分会诊时间:年月日时分会诊意见:科医师签名:彭爱竹手术记录科室:强住院号:LL5Ip姓名:郑俊狗性别:年龄:0?床号:LL5Ip术前诊断:腰椎间盘突出症术后(L4-5);腰椎管狭窄症(L4-5)。术中诊断:腰椎间盘突出症术后(L4-5);腰椎管狭窄症(L4-5)。");
        context.add("2015年08月16时12分23秒手术医师签字:邢泽军入院记录姓名:刘美华出生地:山西太原性别:男职业:无年龄:3岁入院日期:2016年08月民族:汉记录日期:2016年08月婚否:否病历陈述者:母亲主诉:发热伴咳嗽5天。现病史:患儿近5天无明显诱因出现发热,最高体温39.2℃,随后出现咳嗽,咳痰不利,就诊于当地诊所,给予口服\\\"阿奇霉素\\\"3天,祛痰药治疗(具体药名及剂量不详),现体温较前好转,最高体温38.0℃,物理降温后可降至正常,仍有咳嗽,有痰不易咳出,病程中不伴呼吸困难、呕吐、腹泻、皮疹、抽搐等症状,为求诊治入住我科,患儿自发病以来,精神食欲尚可,大小便外观正常。既往史:体健。否认肝炎结核等传染病史,否认手术、外伤史,否认输血史,否认食物、药物过敏史。个人史:第1胎第1产,足月剖宫产,生后无窒息,母乳喂养,按时添加辅食,按序接种过卡介苗、乙肝疫苗、脊髓灰质炎疫苗、百白破疫苗、麻疹疫苗。3月抬头,6月会坐,8月会扶走,周岁走稳。家族史:父母体健,否认家族中遗传病史及传染病史记载。体格检查体温:36.5℃脉搏:126次/分呼吸:26次/分血压:100/56mmHg身高:100cm体重:14.5kg一般情况:发育正常,营养良好,正常面容,神志清楚。皮肤粘膜:全身皮肤无黄染,皮肤弹性良好,无水肿,无皮疹,无皮下结节,无皮下出血点,无瘢痕,无肿块,无蜘蛛痣及肝掌。全身浅表淋巴结:全身浅表淋巴结未触及肿大。头颅:头颅大小及形态正常,无畸形。眼:眼睑无水肿,结膜无充血,巩膜无黄染,双侧瞳孔等大等圆,瞳孔对光反射灵敏。耳:耳廓无畸形,外耳道通畅,无异常分泌物,听力粗测正常。鼻:鼻外形正常,无畸形,鼻翼无煽动,鼻中隔无偏曲。口腔:唇红,口唇无畸形,口腔粘膜无溃疡,无出血点及色素沉着,牙龈无肿胀,无溢脓,咽充血。颈部:颈软,气管居中,甲状腺未触及肿大,颈动脉搏动正常,无颈静脉扩张。胸部:胸廓无畸形。肺脏:视诊:双肺呼吸动度一致,呼吸节律正常,未见肋间隙增宽,变窄。触诊:双肺语颤一致,未触及胸膜摩擦感。叩诊:双肺叩诊清音。听诊:呼吸26次/分,双肺呼吸音粗,未闻及明显干湿性啰音。心:视诊:心前区无隆起,心尖搏动正常。触诊:无抬举性搏动,未及心包摩擦感。未及震颤。叩诊:心界大小正常。听诊:心率126次/分,律齐,各瓣膜听诊区心音正常,未闻及杂音,未闻及心包摩擦音。腹部:视诊:外形平坦,无膨隆、凹陷、瘢痕;未见胃肠型及蠕动波。触诊:腹软,全腹无压痛,反跳痛,肝,脾肋下未触及,未触及包块。叩诊:无移动性浊音。听诊:肠鸣音正常。肛门及外生殖器:未见异常。脊柱及四肢:脊柱、四肢无畸形,肌张力正常,关节无红肿,运动正常,双下肢无浮肿。神经系统:肢体感觉运动正常,生理反射存在,病理反射未引出。辅助检查血常规(2016-8-太原市妇幼门诊):白细胞计数4.95×10^9/L,中性粒细胞44.0%,淋巴细胞46.7%,红细胞计数4.28×10^12/L,血红蛋白119g/l,血小板计数152.0×10^9/L,C反应蛋白10mg/l。胸片(2016-08-我院门诊):双肺感染,右上肺为著。初步诊断:支气管肺炎签名:廖志梅2016年08月入院记录姓名:田钧出生地:河北衡水性别:男职业:个体经营户年龄:48岁入院日期:2016年09月民族:汉族记录日期:2016年09月婚否:已婚病历陈述者:患者本人主诉:头痛伴嗅觉减退3年余现病史:患者于3年余前无明显诱因出现额部疼痛,呈间断性,伴嗅觉减退,无鼻塞、流脓涕、鼻出血等不适,在附近医院给予鼻腔冲洗、口服药物治疗(具体用药不详),效果不佳,6天前就诊于山大二院,行鼻窦CT提示鼻窦炎,建议住院手术,遂于今日就诊于我科门诊,行相关检查,诊断为\\\"慢性鼻窦炎伴鼻息肉\\\",为求进一步治疗收住入院。自发病以来,精神可,食欲佳,体重无明显减轻,大小便正常。既往史:既往体健。否认高血压、心脏病、糖尿病病史,否认肝炎结核等传染病史,否认手术、外伤史,否认输血史,否认食物、药物过敏史,否认预防接种史。个人史:生于河北衡水,现居于太原,未到过疫区,无有害及放射物接触史,目前从事个体经营户,偶有吸烟、饮酒,无冶游史。婚育史:24岁结婚,育有1子1女,配偶体健。家族史:父母均体健,无与患者类似疾病,无家族遗传倾向的疾病。体格检查体温:36.3℃脉搏:71次/分呼吸:18次/分血压:138/89mmHg身高:165cm体重:72.8kg一般情况:发育正常,营养良好,正常面容,神志清楚,自主体位,言语流利,对答切题,查体合作。皮肤粘膜:色泽正常,皮肤弹性良好,无水肿,无皮疹,无皮下结节,无皮下出血点,无瘢痕,无肿块,无蜘蛛痣及肝掌。全身浅表淋巴结:全身浅表淋巴结未触及肿大。头颅:头颅大小及形态正常,无畸形。眼:眼睑无水肿,结膜无充血,巩膜无黄染,双侧瞳孔等大等圆,瞳孔对光反射灵敏。耳:见专科检查。鼻:见专科检查。");
        HashMap<String, HashSet<String>> pre = ner.predict(context);
        System.out.println();


    }


}
