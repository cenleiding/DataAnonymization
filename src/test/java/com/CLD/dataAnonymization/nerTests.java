package com.CLD.dataAnonymization;

import org.tensorflow.SavedModelBundle;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/8/21 13:22
 */
public class nerTests {

    public static void main(String[] args) {
        SavedModelBundle b = SavedModelBundle.load("../re/NER", "NER");
    }
}
