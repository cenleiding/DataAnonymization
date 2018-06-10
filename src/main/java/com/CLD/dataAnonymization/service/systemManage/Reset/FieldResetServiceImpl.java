package com.CLD.dataAnonymization.service.systemManage.Reset;

import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author CLD
 * @Date 2018/6/10 16:32
 **/
@Service
public class FieldResetServiceImpl implements  FieldResetService {

    @Autowired
    FieldClassifyService fieldClassifyService;

    @Override
    public Boolean FieldReset() {
        return fieldClassifyService.FileToDB();
    }
}
