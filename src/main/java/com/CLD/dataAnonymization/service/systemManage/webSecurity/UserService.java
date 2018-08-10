package com.CLD.dataAnonymization.service.systemManage.webSecurity;

import com.CLD.dataAnonymization.dao.h2.entity.SystemUser;
import com.CLD.dataAnonymization.dao.h2.repository.SystemUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/10 16:16
 */
@Service
public class UserService implements UserDetailsService {

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
}
