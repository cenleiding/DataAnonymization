package com.CLD.dataAnonymization;

import com.CLD.dataAnonymization.service.nodeAndField.nodeToField.NodeToFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * 该了用于定时任务
 * @Author CLD
 * @Date 2018/5/15 19:08
 **/
@Component
@Slf4j
public class ScheduledTasks {


    @Autowired
    NodeToFieldService nodeToFieldService;

    /**
     * 定时更新原型表
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    @Scheduled(initialDelay = 10000,fixedDelay = 86400000)
    public void updataFields() throws FileNotFoundException, UnsupportedEncodingException {
        log.info("openEhr 字段更新");
         nodeToFieldService.ArcheTypeNodeToField();
    }
}
