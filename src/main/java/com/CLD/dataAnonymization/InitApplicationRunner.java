package com.CLD.dataAnonymization;

import com.CLD.dataAnonymization.dao.h2.repository.UsageFieldClassifyRepository;
import com.CLD.dataAnonymization.service.systemManage.reset.NodeResetService;
import com.CLD.dataAnonymization.service.systemManage.initialize.ResourcesFileInitializeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 启动运行初始化
 * @Author CLD
 * @Date 2018/4/26 14:57
 **/
@Component
@Order(value = 1)
public class InitApplicationRunner implements ApplicationRunner{

    @Autowired
    ResourcesFileInitializeService resourcesFileInitializeService;

    @Autowired
    NodeResetService nodeResetService;

    @Autowired
    UsageFieldClassifyRepository usageFieldClassifyRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //文件初始化
        resourcesFileInitializeService.InitializeResourcesFile();

        //字段表，Original表创建
        List<String> fieldList=usageFieldClassifyRepository.getFromName();
        if(fieldList.size()==0)
        nodeResetService.NodeReset();
    }
}
