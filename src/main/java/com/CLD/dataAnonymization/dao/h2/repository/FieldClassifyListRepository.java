package com.CLD.dataAnonymization.dao.h2.repository;

import com.CLD.dataAnonymization.dao.h2.entity.FieldClassifyList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @description:
 * @Author CLD
 * @Date 2018/8/15 12:20
 */
@Repository
public interface FieldClassifyListRepository extends JpaRepository<FieldClassifyList,Long> {

    @Query(value = "select distinct f.userName from FieldClassifyList f")
    public List<String> getUserName();

    @Query(value = "select f.formName from FieldClassifyList f where f.userName=?1")
    public List<String> getFormNameByUserName(String userName);

    @Transactional
    @Modifying
    public void deleteByFormName(String formName);

    public List<FieldClassifyList> findByUserName(String userName);

    public FieldClassifyList findByFormName(String formName);

}
