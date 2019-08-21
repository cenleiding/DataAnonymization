package com.CLD.dataAnonymization.util.deidentifier.algorithm;


import org.tensorflow.*;

import java.io.IOException;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/8/21 13:37
 */
public class NER {

    Session tfSession = null;

    public NER(){
        SavedModelBundle b = SavedModelBundle.load("./src/main/java/com/CLD/dataAnonymization/util/deidentifier/resources/NER","NER");
        tfSession = b.session();
    }

    public int[][] predict(int[][] inputs,int[] lengths){
        int[][] labels = new int[inputs.length][inputs[0].length];
        int[][] result = new int[inputs.length][inputs[0].length];
        Tensor input_x = Tensor.create(inputs);
        Tensor input_l = Tensor.create(lengths);
        Tensor input_b = Tensor.create(labels);
        Tensor<?> out = tfSession.runner()
                .feed("inputs",input_x)
                .feed("lengths",input_l)
                .feed("labels",input_b)
                .fetch("predict")
                .run().get(0);
        out.copyTo(result);
        return result;
    }

    public static void main(String[] args) throws IOException {
        int[][] inputs = new int[2][100];
        inputs[0] = new int[]{20,22,223,73,103,1130,2085,41,138,2,6,180,115,17,132,5,5,2,24,55,55,5,24,156,728,18,371,136,1118,691,96,47,103,1130,2085,297,418,19,297,418,132,2,14,5,21,55,2,6,5,5,14,5,29,2,14,6,6,2,58,409,113,47,350,20,22,114,246,297,418,19,297,418,132,34,17,74,265,400};
        inputs[1] = new int[]{20,22,223,73,103,1130,2085,41,138,2,6,180,115,17,132,5,5,2,24,55,55,5,24,156,728,18,371,136,1118,691,96,47,103,1130,2085,297,418,19,297,418,132,2,14,5,21,55,2,6,5,5,14,5,29,2,14,6,6,2,58,409,113,47,350,20,22,114,246,297,418,19,297,418,132,34,17,74,265,400};
        int[] l = new int[]{78,78};
        NER ner= new NER();
        int[][] result =  ner.predict(inputs,l);
        for(int i:result[0])
            System.out.print(i);
        System.out.println();
        for(int i:result[1])
            System.out.print(i);

    }


}
