package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.Regular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/22 12:26
 */
@Repository
public interface RegularRepository extends JpaRepository<Regular,Long> {

}
