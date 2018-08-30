package com.CLD.dataAnonymization.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @description:该类用于存储从模板接口中获得的原型节点信息
 * @Author CLD
 * @Date 2018/5/10 9:22
 **/
@Getter
@Setter
public class TemplateNodeInfo {

    private String NodePath;

    private String specialiseArchetypeId;

    private String En_name;

    private String Ch_name;

}
