package com.CLD.dataAnonymization.service.systemManage.reset;

import java.io.FileNotFoundException;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/22 14:17
 */
public interface RegularAndDictionaryResetService {

    /**
     * 重置规则库，字典和规则。
     * @return
     */
    public Boolean RegularAndDictionaryReset() throws FileNotFoundException;
}
