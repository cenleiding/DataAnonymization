package com.CLD.dataAnonymization.service.deidentifyTarget.apiDeidentify;

import com.CLD.dataAnonymization.model.AnonymizeConfigure;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @description:该类用于提供API数据匿名化服务
 * @Author CLD
 * @Date 2018/5/27 13:55
 **/
public interface ApiDeidentifyService {

    /**
     * Api数据匿名化
     * @param req
     * @param anonymizeConfigure
     * @return
     */
    public ArrayList<HashMap<String,String>> ApiDataDeidentify(HttpServletRequest req, AnonymizeConfigure anonymizeConfigure);
}
