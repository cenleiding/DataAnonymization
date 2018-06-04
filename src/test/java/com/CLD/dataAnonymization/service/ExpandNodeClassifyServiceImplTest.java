package com.CLD.dataAnonymization.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author CLD
 * @Date 2018/6/4 11:08
 **/
public class ExpandNodeClassifyServiceImplTest {
    @Autowired
    ExpandNodeClassifyService expandNodeClassifyService;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void fileToDB() throws Exception {
        expandNodeClassifyService.FileToDB();
    }

    @Test
    public void DBToFile() throws Exception {
    }

    @Test
    public void getFileName() throws Exception {
    }

    @Test
    public void getFromNameByFileName() throws Exception {
    }

    @Test
    public void getNodeInfoByName() throws Exception {
    }

    @Test
    public void updataNodeInfo() throws Exception {
    }

}