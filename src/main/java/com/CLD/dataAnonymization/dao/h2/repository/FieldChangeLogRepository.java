package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.FieldChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/15 12:21
 */
@Repository
public interface FieldChangeLogRepository extends JpaRepository<FieldChangeLog,Long> {
}
