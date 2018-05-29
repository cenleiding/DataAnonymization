package com.CLD.dataAnonymization.service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 该类用于提供API数据匿名化服务
 * @Author CLD
 * @Date 2018/5/27 13:55
 **/
public interface ApiDeidentifyService {

    /**
     * 该类对Api数据进行安全屋处理
     * @param req
     * @param fieldFromName
     * @return
     */
    public ArrayList<HashMap<String,String>> ApiSafeHarbor(HttpServletRequest req, String fieldFromName);

    /**
     * 该类对Api数据进行有限数据集处理
     * @param req
     * @param fieldFromName
     * @return
     */
    public ArrayList<HashMap<String,String>> ApiLimitedSet(HttpServletRequest req,String fieldFromName);
}
