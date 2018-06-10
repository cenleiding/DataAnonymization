package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.ExpandNodeClassify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author CLD
 * @Date 2018/6/3 15:40
 **/
@Repository
public interface ExpandNodeClassifyRepository extends JpaRepository<ExpandNodeClassify,Long>{

    @Query(value = "select distinct n.expandName from ExpandNodeClassify n")
    public List<String> getExpandName();

    @Query(value = "select distinct n.fromName from ExpandNodeClassify n where n.expandName=?1")
    public List<String> getFromNameByExpandName(String expandName);

    public List<ExpandNodeClassify> findByExpandNameAndFromName(String expandName,String fromName);

    public List<ExpandNodeClassify> findByExpandNameIsNotOrFromNameIsNot(String expandName,String fromName);

}
