package com.CLD.dataAnonymization.service.systemManage.backUp;

import com.CLD.dataAnonymization.service.nodeAndField.fieldClassify.FieldClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author CLD
 * @Date 2018/6/10 15:54
 **/
@Service
public class FieldFileBackUpServiceImpl implements FieldFileBackUpService {

    @Autowired
    FieldClassifyService fieldClassifyService;

    @Override
    public Boolean FieldFileBackUp() {
        return fieldClassifyService.DBToFile();
    }
}
