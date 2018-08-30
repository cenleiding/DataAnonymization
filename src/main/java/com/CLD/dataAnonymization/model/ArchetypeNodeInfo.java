package com.CLD.dataAnonymization.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 该类用于存储完整的节点信息
 * @Author CLD
 * @Date 2018/5/22 15:56
 **/
@Getter
@Setter
public class ArchetypeNodeInfo {

    private String id;

    private String nodeName;

    private String nodePath;

    private String nodeType;

    private String archetypeName;

    private String archetypeId;

    private String description;

}
