package com.CLD.dataAnonymization;

import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyListRepository;
import com.CLD.dataAnonymization.dao.h2.repository.SystemUserRepository;
import com.CLD.dataAnonymization.service.systemManage.initialize.ResourcesFileInitializeService;
import com.CLD.dataAnonymization.service.systemManage.mergeField.MergeFieldService;
import com.CLD.dataAnonymization.service.systemManage.reset.NodeResetService;
import com.CLD.dataAnonymization.service.systemManage.webSecurity.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${original.userName}")
    private String userName;

    @Value("${original.password}")
    private String password;

    @Autowired
    ResourcesFileInitializeService resourcesFileInitializeService;

    @Autowired
    NodeResetService nodeResetService;

    @Autowired
    SystemUserRepository systemUserRepository;

    @Autowired
    FieldClassifyListRepository fieldClassifyListRepository;

    @Autowired
    MergeFieldService mergeFieldService;

    @Autowired
    UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //文件初始化
        resourcesFileInitializeService.InitializeResourcesFile();

        //基础节点初始化
        nodeResetService.NodeReset();

        //生成合并总表
        System.out.println("合并总表："+mergeFieldService.mergeAllField());

        //用户初始化
        if(userService.getUser().size()==0)
        userService.addUser(userName,password);

    }

}
