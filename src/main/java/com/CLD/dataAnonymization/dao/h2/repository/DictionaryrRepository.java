package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/22 12:27
 */
@Repository
public interface DictionaryrRepository extends JpaRepository<Dictionary,Long>{

    public List<String> getFileNameByLibName(String libName);

    @Transactional
    @Modifying
    public void deleteByLibName(String libName);
}
