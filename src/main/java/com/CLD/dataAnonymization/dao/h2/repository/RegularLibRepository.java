package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.RegularLib;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/22 12:25
 */
@Repository
public interface RegularLibRepository extends JpaRepository<RegularLib,Long> {

    public RegularLib findByLibName(String libName);

    @Query(value = "select r.libName from RegularLib r")
    public List<String> getLibName();

    public List<RegularLib> findAllByUser(String user);

    @Transactional
    @Modifying
    public void deleteByLibName(String libName);

    @Query(value = "select r from RegularLib r")
    public List<RegularLib> getAll();

}
