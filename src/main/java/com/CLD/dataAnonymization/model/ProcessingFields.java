package com.CLD.dataAnonymization.model;


import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 用于处理的匿名字段名
 * @Author CLD
 * @Date 2018/4/11 9:03
 **/
@Component
public class ProcessingFields {

    public ArrayList<String> sensitive=new ArrayList<String>();

    public ArrayList<String> date=new ArrayList<String>();

    public ArrayList<String> geographic=new ArrayList<String>();

    public ArrayList<String> other_middle=new ArrayList<String>();

    public ArrayList<String> other_soft=new ArrayList<String>();

    public ArrayList<String> Unstructured=new ArrayList<String>();


    public ArrayList<String> getSensitive() {
        return sensitive;
    }

    public void setSensitive(ArrayList<String> sensitive) {
        this.sensitive = sensitive;
    }

    public ArrayList<String> getDate() {
        return date;
    }

    public void setDate(ArrayList<String> date) {
        this.date = date;
    }

    public ArrayList<String> getGeographic() {
        return geographic;
    }

    public void setGeographic(ArrayList<String> geographic) {
        this.geographic = geographic;
    }

    public ArrayList<String> getOther_middle() {
        return other_middle;
    }

    public void setOther_middle(ArrayList<String> other_middle) {
        this.other_middle = other_middle;
    }

    public ArrayList<String> getOther_soft() {
        return other_soft;
    }

    public void setOther_soft(ArrayList<String> other_soft) {
        this.other_soft = other_soft;
    }

    public ArrayList<String> getUnstructured() {
        return Unstructured;
    }

    public void setUnstructured(ArrayList<String> unstructured) {
        Unstructured = unstructured;
    }
}
