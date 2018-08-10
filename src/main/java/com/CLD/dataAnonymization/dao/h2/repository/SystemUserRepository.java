package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/10 16:09
 */
@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser,Long> {

    public SystemUser findByUsername(String username);
}
