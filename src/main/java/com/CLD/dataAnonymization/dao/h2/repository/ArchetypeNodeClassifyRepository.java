package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.ArchetypeNodeClassify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Author CLD
 * @Date 2018/5/21 20:58
 **/
@Repository
public interface ArchetypeNodeClassifyRepository extends JpaRepository<ArchetypeNodeClassify,Long> {

    @Query(value = "select distinct n.archetypeName from ArchetypeNodeClassify n")
    public List<String> getArchetypeName();

    public List<ArchetypeNodeClassify> findByArchetypeName(String archetypeName);

    @Transactional
    @Modifying
    public Long deleteByArchetypeName (String archetypeName);

    public List<ArchetypeNodeClassify> findByArchetypeNameIsNot(String archetypeName);

}
