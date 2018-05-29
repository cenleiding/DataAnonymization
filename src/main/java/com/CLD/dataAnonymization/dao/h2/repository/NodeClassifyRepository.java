package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.NodeClassify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Author CLD
 * @Date 2018/5/21 20:58
 **/
public interface NodeClassifyRepository extends JpaRepository<NodeClassify,Long> {

    @Query(value = "select distinct n.archetypeName from NodeClassify n")
    public List<String> getArchetypeName();

    public List<NodeClassify> findByArchetypeName(String archetypeName);

    @Transactional
    @Modifying
    public Long deleteByArchetypeName (String archetypeName);

    public List<NodeClassify> findByArchetypeNameIsNot(String archetypeName);

}
