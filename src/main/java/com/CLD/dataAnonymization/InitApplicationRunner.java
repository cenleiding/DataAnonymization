package com.CLD.dataAnonymization;

import com.CLD.dataAnonymization.service.ExpandNodeClassifyService;
import com.CLD.dataAnonymization.service.NodeToFieldService;
import com.CLD.dataAnonymization.service.OpenEhrNodeClassifyService;
import com.CLD.dataAnonymization.service.ResourcesFileInitializeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * 启动运行初始化
 * @Author CLD
 * @Date 2018/4/26 14:57
 **/
@Component
@Order(value = 1)
public class InitApplicationRunner implements ApplicationRunner{

    @Autowired
    NodeToFieldService nodeToFieldService;

    @Autowired
    OpenEhrNodeClassifyService openEhrNodeClassifyService;

    @Autowired
    ResourcesFileInitializeService resourcesFileInitializeService;

    @Autowired
    ExpandNodeClassifyService expandNodeClassifyService;




    @Override
    public void run(ApplicationArguments args) throws Exception {

        resourcesFileInitializeService.InitializeResourcesFile();

      expandNodeClassifyService.FileToDB();
      openEhrNodeClassifyService.FileToDB();

      nodeToFieldService.ArcheTypeNodeToField();
      nodeToFieldService.ExpandNodeToField();





         /////////////test
        //openEhrNodeClassifyService.FileToDB();


    }


}
