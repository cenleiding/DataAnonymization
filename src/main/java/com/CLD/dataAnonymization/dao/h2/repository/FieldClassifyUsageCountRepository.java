package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.FieldClassifyUsageCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/16 14:31
 */
@Repository
public interface FieldClassifyUsageCountRepository extends JpaRepository<FieldClassifyUsageCount,Long>{

    @Transactional
    @Modifying
    public void deleteByFormName(String formName);

    public FieldClassifyUsageCount findByFormName(String formName);
}
