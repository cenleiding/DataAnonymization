package com.CLD.dataAnonymization.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author CLD
 * @Date 2018/6/4 10:25
 **/
@Getter
@Setter
public class ExpandNodeInfo {

    private Long id;

    private String expandName;

    private String fromName;

    private String CH_name;

    private String EN_name;

    private String description;

    private String nodeType;
}
