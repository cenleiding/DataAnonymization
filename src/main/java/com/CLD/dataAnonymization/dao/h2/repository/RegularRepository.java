package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.Regular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/22 12:26
 */
@Repository
public interface RegularRepository extends JpaRepository<Regular,Long> {


    @Transactional
    @Modifying
    public void deleteByLibName(String libName);

    public List<Regular> findByLibName(String libName);

    @Transactional
    @Modifying
    public void deleteById(Long id);


}
