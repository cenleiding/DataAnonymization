package com.CLD.dataAnonymization.service.systemManage.webSecurity;

import com.CLD.dataAnonymization.dao.h2.entity.SystemUser;

import java.util.List;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/15 14:59
 */
public interface UserService {

    public Boolean addUser(String username,String password);

    public List<SystemUser> getUser();
}
