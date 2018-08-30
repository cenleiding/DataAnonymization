package com.CLD.dataAnonymization.service.systemManage.reset;

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

    @Override
    public List<String> NodeReset() {
        List<String> outList=new ArrayList<String>();
        try {
            openEhrNodeClassifyService.FileToDB();
            expandNodeClassifyService.FileToDB();
            outList=nodeToFieldService.ArcheTypeNodeToField();
            outList.addAll(nodeToFieldService.ExpandNodeToField());
        }catch (Exception e){
            e.printStackTrace();
        }

        return outList;
    }
}
