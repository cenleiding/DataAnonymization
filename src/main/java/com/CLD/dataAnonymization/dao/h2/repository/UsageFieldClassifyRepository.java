package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.UsageFieldClassify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Author CLD
 * @Date 2018/5/21 20:56
 **/
@Repository
public interface UsageFieldClassifyRepository extends JpaRepository<UsageFieldClassify,Long> {


    @Transactional
    @Modifying
    public void deleteByFromName(String fromName);

    public List<UsageFieldClassify> findByFromName(String fromName);

    @Query(value = "select distinct f.fromName  from UsageFieldClassify f ")
    public List<String> getFromName();

}
