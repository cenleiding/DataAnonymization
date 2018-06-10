package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.ArchetypeBasisFieldClassify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author CLD
 * @Date 2018/6/3 15:36
 **/
@Repository
public interface ArchetypeBasisFieldClassifyRepository extends JpaRepository<ArchetypeBasisFieldClassify,Long>{
}
