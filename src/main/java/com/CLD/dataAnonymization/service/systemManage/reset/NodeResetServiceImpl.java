package com.CLD.dataAnonymization.service.systemManage.reset;

import com.CLD.dataAnonymization.dao.h2.repository.ArchetypeBasisFieldClassifyRepository;
import com.CLD.dataAnonymization.dao.h2.repository.ExpandBasisFieldClassifyRepository;
import com.CLD.dataAnonymization.service.nodeAndField.nodeClassify.ExpandNodeClassifyService;
import com.CLD.dataAnonymization.service.nodeAndField.nodeClassify.OpenEhrNodeClassifyService;
import com.CLD.dataAnonymization.service.nodeAndField.nodeToField.NodeToFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author CLD
 * @Date 2018/6/5 16:01
 **/
@Service
public class NodeResetServiceImpl implements NodeResetService {

    @Autowired
    OpenEhrNodeClassifyService openEhrNodeClassifyService;

    @Autowired
    ExpandNodeClassifyService expandNodeClassifyService;

    @Autowired
    NodeToFieldService nodeToFieldService;

    @Autowired
    ArchetypeBasisFieldClassifyRepository archetypeBasisFieldClassifyRepository;

    @Autowired
    ExpandBasisFieldClassifyRepository expandBasisFieldClassifyRepository;

    @Override
    public List<String> NodeReset() {
        List<String> outList=new ArrayList<String>();
        openEhrNodeClassifyService.FileToDB();
        expandNodeClassifyService.FileToDB();
        archetypeBasisFieldClassifyRepository.deleteAll();
        expandBasisFieldClassifyRepository.deleteAll();
        outList=nodeToFieldService.ArcheTypeNodeToField();
        outList.addAll(nodeToFieldService.ExpandNodeToField());
        return outList;
    }
}
