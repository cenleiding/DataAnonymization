package com.CLD.dataAnonymization.dao.mysql.repository;


import com.CLD.dataAnonymization.dao.mysql.entity.ApiUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Author CLD
 * @Date 2018/4/25 10:55
 **/
public interface UsageRepository extends JpaRepository<ApiUsage,Long>{

    public List<ApiUsage> findAllByIp(String ip);

    @Query(value = "Select a.ip from  ApiUsage a ")
    public List<String> findAllIp();

}
