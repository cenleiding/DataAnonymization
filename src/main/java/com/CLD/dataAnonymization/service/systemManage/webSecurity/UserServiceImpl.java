package com.CLD.dataAnonymization.service.systemManage.webSecurity;

import com.CLD.dataAnonymization.dao.h2.entity.SystemUser;
import com.CLD.dataAnonymization.dao.h2.repository.SystemUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/10 16:16
 */
@Service
public class UserServiceImpl implements UserDetailsService,UserService {

    @Autowired
    SystemUserRepository systemUserRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        SystemUser systemUser=systemUserRepository.findByUsername(s);
        if (systemUser == null) {
            throw new UsernameNotFoundException("username is not exists");
        }
        return systemUser;
    }

    public Boolean addUser(String username,String password){
        SystemUser systemUser=systemUserRepository.findByUsername(username);
        if (systemUser == null || username.trim()!="" || password.trim()!="") {
            SystemUser newUser=new SystemUser();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole("USER");
            systemUserRepository.save(newUser);
           return true;
        }
        return false;
    }

    @Override
    public List<SystemUser> getUser() {
        return systemUserRepository.findAll();
    }
}
