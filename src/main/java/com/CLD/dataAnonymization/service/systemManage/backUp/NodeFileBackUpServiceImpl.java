package com.CLD.dataAnonymization.service.systemManage.backUp;

import com.CLD.dataAnonymization.service.nodeAndField.nodeClassify.ExpandNodeClassifyService;
import com.CLD.dataAnonymization.service.nodeAndField.nodeClassify.OpenEhrNodeClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author CLD
 * @Date 2018/6/5 15:18
 **/
@Service
public class NodeFileBackUpServiceImpl implements NodeFileBackUpService {

    @Autowired
    OpenEhrNodeClassifyService openEhrNodeClassifyService;

    @Autowired
    ExpandNodeClassifyService expandNodeClassifyService;

    @Override
    public Boolean NodeFileBackUp() {
        openEhrNodeClassifyService.DBToFile();
        expandNodeClassifyService.DBToFile();
        return true;
    }
}
