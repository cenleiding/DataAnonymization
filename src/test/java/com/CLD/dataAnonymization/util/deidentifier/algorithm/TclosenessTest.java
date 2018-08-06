package com.CLD.dataAnonymization.util.deidentifier.algorithm;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/6 13:37
 */
public class TclosenessTest {

    @Test
    public void tClosenessHandle() throws Exception {

        ArrayList<ArrayList<String>> data=new ArrayList<ArrayList<String>> ();
        ArrayList<String> zipcode=new ArrayList<String>();
        String[] S=new String[]{"zipcode","47677","47602","47678","47905","47909","47906","47605","47673","47607"};
        Collections.addAll(zipcode,S);
        ArrayList<String> age=new ArrayList<String>();
        S=new String[]{"age","29","22","27","43","52","47","30","36","32"};
        Collections.addAll(age,S);
        ArrayList<String> disease=new ArrayList<String>();
        S=new String[]{"disease","gastric ulcer","gastritis","stomach cancer","gastritis","flu","bronchitis","bronchitis","pneumonia","stomach cancer"};
        Collections.addAll(disease,S);
        data.add(zipcode);
        data.add(age);
        data.add(disease);


        ArrayList<Integer> kcol=new ArrayList<Integer>();
        kcol.add(0);kcol.add(1);
        ArrayList<Integer> tcol=new ArrayList<Integer>();
        tcol.add(2);


        HashMap<String,ArrayList<ArrayList<String>>> hierarchy =new HashMap<String,ArrayList<ArrayList<String>>> ();
        ArrayList<ArrayList<String>> ageh=new ArrayList<ArrayList<String>>();
        ArrayList<String> h=new ArrayList<String>();
        Collections.addAll(h,new String[]{"29", "<=40", "*"});
        ageh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"22", "<=40", "*"});
        ageh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"27", "<=40", "*"});
        ageh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"43", ">40", "*"});
        ageh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"52", ">40", "*"});
        ageh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"47", ">40", "*"});
        ageh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"30", "<=40", "*"});
        ageh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"36", "<=40", "*"});
        ageh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"32", "<=40", "*"});
        ageh.add((ArrayList<String>) h.clone());
        h.clear();

        ArrayList<ArrayList<String>> zipcodeh=new ArrayList<ArrayList<String>>();
        Collections.addAll(h,new String[]{"47677", "4767*", "476**", "47***", "4****", "*****"});
        zipcodeh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"47602", "4760*", "476**", "47***", "4****", "*****"});
        zipcodeh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"47678", "4767*", "476**", "47***", "4****", "*****"});
        zipcodeh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"47905", "4790*", "479**", "47***", "4****", "*****"});
        zipcodeh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"47909", "4790*", "479**", "47***", "4****", "*****"});
        zipcodeh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"47906", "4790*", "479**", "47***", "4****", "*****"});
        zipcodeh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"47605", "4760*", "476**", "47***", "4****", "*****"});
        zipcodeh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"47673", "4767*", "476**", "47***", "4****", "*****"});
        zipcodeh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"47607", "4760*", "476**", "47***", "4****", "*****"});
        zipcodeh.add((ArrayList<String>) h.clone());
        h.clear();

        ArrayList<ArrayList<String>> diseaseh=new ArrayList<ArrayList<String>>();
        Collections.addAll(h,new String[]{"flu",
                "respiratory infection",
                "vascular lung disease",
                "respiratory & digestive system disease"});
        diseaseh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"pneumonia",
                "respiratory infection",
                "vascular lung disease",
                "respiratory & digestive system disease"});
        diseaseh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"bronchitis",
                "respiratory infection",
                "vascular lung disease",
                "respiratory & digestive system disease"});
        diseaseh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"pulmonary edema",
                "vascular lung disease",
                "vascular lung disease",
                "respiratory & digestive system disease"});
        diseaseh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"pulmonary embolism",
                "vascular lung disease",
                "vascular lung disease",
                "respiratory & digestive system disease"});
        diseaseh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"gastric ulcer",
                "stomach disease",
                "digestive system disease",
                "respiratory & digestive system disease"});
        diseaseh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"stomach cancer",
                "stomach disease",
                "digestive system disease",
                "respiratory & digestive system disease"});
        diseaseh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"gastritis",
                "stomach disease",
                "digestive system disease",
                "respiratory & digestive system disease"});
        diseaseh.add((ArrayList<String>) h.clone());
        h.clear();
        Collections.addAll(h,new String[]{"colitis",
                "colon disease",
                "digestive system disease",
                "respiratory & digestive system disease"});
        diseaseh.add((ArrayList<String>) h.clone());
        h.clear();

        hierarchy.put("age",ageh);
        hierarchy.put("zipcode",zipcodeh);
        hierarchy.put("disease",diseaseh);

        Tcloseness.tClosenessHandle(data,kcol,tcol,2,0.6,0.0,hierarchy);
    }

}