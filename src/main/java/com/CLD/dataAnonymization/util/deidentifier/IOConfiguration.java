package com.CLD.dataAnonymization.util.deidentifier;

import com.CLD.dataAnonymization.model.ProcessingFields;
import com.CLD.dataAnonymization.service.PrivacyFieldService;
import com.CLD.dataAnonymization.service.PrivacyFieldServiceImpl;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * 此类用于外部调用匿名方法
 * 并进行参数设置
 * @Author CLD
 * @Date 2018/4/12 10:14
 **/
public class IOConfiguration {

    /**用于外部调用安全屋方法
     * @param data
     * @param fields
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<ArrayList<String>> ToSafeHarbor(ArrayList<ArrayList<String>> data, ProcessingFields fields) throws FileNotFoundException {
        SafeHarbor safeHarbor=new SafeHarbor();
        ArrayList<String> removeField=fields.getSensitive();
        removeField.addAll(fields.getOther_soft());
        data=safeHarbor.identity(data,removeField,fields.getDate(),fields.getGeographic(),
                                 fields.getOther_middle(),fields.getUnstructured());
        return data;
    }

    /**用于外部调用安全屋方法
     * @param data
     * @param data
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<ArrayList<String>> ToSafeHarbor(ArrayList<ArrayList<String>> data) throws FileNotFoundException {
        PrivacyFieldService privacyFieldService=new PrivacyFieldServiceImpl();
        ProcessingFields fields= privacyFieldService.getProcessingFields();
        SafeHarbor safeHarbor=new SafeHarbor();
        ArrayList<String> removeField=fields.getSensitive();
        removeField.addAll(fields.getOther_soft());
        data=safeHarbor.identity(data,removeField,fields.getDate(),fields.getGeographic(),
                fields.getOther_middle(),fields.getUnstructured());
        return data;
    }

    /**
     * 用于外部调用有限数据集方法
     * @param data
     * @param fields
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<ArrayList<String>> ToLimitedSet(ArrayList<ArrayList<String>> data, ProcessingFields fields) throws FileNotFoundException {
        LimitedSet limitedSet=new LimitedSet();
        data=limitedSet.identity(data,fields.getSensitive(),fields.getDate(),fields.getGeographic(),fields.getUnstructured());
        return data;
    }

    /**
     * 用于外部调用有限数据集方法
     * @param data
     * @param data
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<ArrayList<String>> ToLimitedSet(ArrayList<ArrayList<String>> data) throws FileNotFoundException {
        PrivacyFieldService privacyFieldService=new PrivacyFieldServiceImpl();
        ProcessingFields fields= privacyFieldService.getProcessingFields();
        LimitedSet limitedSet=new LimitedSet();
        data=limitedSet.identity(data,fields.getSensitive(),fields.getDate(),fields.getGeographic(),fields.getUnstructured());
        return data;
    }

}
