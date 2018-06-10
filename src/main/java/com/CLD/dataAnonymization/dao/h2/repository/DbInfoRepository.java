package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.DbInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Author CLD
 * @Date 2018/5/31 16:36
 **/
@Repository
public interface DbInfoRepository extends JpaRepository<DbInfo,Long> {

    public List<DbInfo> findAllByUrl(String url);

    @Transactional
    @Modifying
    public void deleteAllByUrl(String url);
}
