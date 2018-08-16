package com.CLD.dataAnonymization;

import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyListRepository;
import com.CLD.dataAnonymization.dao.h2.repository.SystemUserRepository;
import com.CLD.dataAnonymization.dao.h2.repository.UsageFieldClassifyRepository;
import com.CLD.dataAnonymization.service.systemManage.initialize.ResourcesFileInitializeService;
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
    UsageFieldClassifyRepository usageFieldClassifyRepository;

    @Autowired
    SystemUserRepository systemUserRepository;

    @Autowired
    FieldClassifyListRepository fieldClassifyListRepository;

    @Autowired
    UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //文件初始化
        resourcesFileInitializeService.InitializeResourcesFile();
//
//        //字段表，Original表创建
//        List<String> fieldList=usageFieldClassifyRepository.getFormName();
//        if(fieldList.size()==0)
        nodeResetService.NodeReset();

        //用户初始化
        if(userService.getUser().size()==0)
        userService.addUser(userName,password);

    }

}
