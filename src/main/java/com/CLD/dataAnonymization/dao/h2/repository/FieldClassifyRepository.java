package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.FieldClassify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/15 12:19
 */
@Repository
public interface FieldClassifyRepository extends JpaRepository<FieldClassify,Long> {

    @Transactional
    @Modifying
    public void deleteByFromName(String fromName);
}
