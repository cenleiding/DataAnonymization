package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.FieldClassify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Author CLD
 * @Date 2018/5/21 20:56
 **/
public interface FieldClassifyRepository extends JpaRepository<FieldClassify,Long> {


    @Transactional
    @Modifying
    public void deleteByFromName(String fromName);

    public List<FieldClassify> findByFromName(String fromName);

    @Query(value = "select distinct f.fromName  from FieldClassify f ")
    public List<String> getFromName();

}
