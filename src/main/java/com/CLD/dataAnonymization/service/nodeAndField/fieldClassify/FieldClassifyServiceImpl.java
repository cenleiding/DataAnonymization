package com.CLD.dataAnonymization.service.nodeAndField.fieldClassify;

import com.CLD.dataAnonymization.dao.h2.entity.FieldChangeLog;
import com.CLD.dataAnonymization.dao.h2.entity.FieldClassify;
import com.CLD.dataAnonymization.dao.h2.entity.FieldClassifyList;
import com.CLD.dataAnonymization.dao.h2.entity.FieldClassifyUsageCount;
import com.CLD.dataAnonymization.dao.h2.repository.FieldChangeLogRepository;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyListRepository;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyRepository;
import com.CLD.dataAnonymization.dao.h2.repository.FieldClassifyUsageCountRepository;
import com.CLD.dataAnonymization.model.FieldFormInfo;
import com.CLD.dataAnonymization.model.FieldInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description:该类用于生成、操作可用匿名字段表
 * @Author CLD
 * @Date 2018/5/23 9:08
 **/
@Service
public class FieldClassifyServiceImpl implements FieldClassifyService {

    @Autowired
    FieldClassifyListRepository fieldClassifyListRepository;

    @Autowired
    FieldClassifyRepository fieldClassifyRepository;

    @Autowired
    FieldClassifyUsageCountRepository fieldClassifyUsageCountRepository;

    @Autowired
    FieldChangeLogRepository fieldChangeLogRepository;

    @Override
    public List<FieldFormInfo> getFieldFormInfo(){
        List<FieldFormInfo> fieldFormInfoList =new ArrayList<FieldFormInfo>();
        List<FieldClassifyList> fieldClassifyListList=fieldClassifyListRepository.findAll();
        for(FieldClassifyList fieldClassifyList:fieldClassifyListList){
            FieldClassifyUsageCount fieldClassifyUsageCount=fieldClassifyUsageCountRepository.findByFormName(fieldClassifyList.getFormName());
            List<FieldChangeLog> fieldChangeLogList=fieldChangeLogRepository.findByFormName(fieldClassifyList.getFormName());
            Long createTime=Long.MAX_VALUE;
            Long lastChangeTime= Long.MIN_VALUE;
            for(FieldChangeLog fieldChangeLog:fieldChangeLogList){
                if(fieldChangeLog.getDateTime().getTime()<createTime) createTime=fieldChangeLog.getDateTime().getTime();
                if(fieldChangeLog.getDateTime().getTime()>lastChangeTime) lastChangeTime=fieldChangeLog.getDateTime().getTime();
            }
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            FieldFormInfo fieldFormInfo =new FieldFormInfo();
            fieldFormInfo.setUserName(fieldClassifyList.getUserName());
            fieldFormInfo.setFormName(fieldClassifyList.getFormName());
            fieldFormInfo.setCreateTime(sdf.format(new Date(createTime)));
            fieldFormInfo.setLastChangeTime(sdf.format(new Date(lastChangeTime)));
            fieldFormInfo.setDescription(fieldClassifyList.getDescription());
            fieldFormInfo.setFather(fieldClassifyList.getFather());
            fieldFormInfo.setUsageCount(String.valueOf(fieldClassifyUsageCount.getCount()));

            fieldFormInfoList.add(fieldFormInfo);
        }
        return fieldFormInfoList;
    }

    @Override
    public FieldFormInfo getFieldFormInfoByFormName(String formName){
        FieldFormInfo fieldFormInfo=new FieldFormInfo();
        FieldClassifyList fieldClassifyList=fieldClassifyListRepository.findByFormName(formName);
        FieldClassifyUsageCount fieldClassifyUsageCount=fieldClassifyUsageCountRepository.findByFormName(formName);
        List<FieldChangeLog> fieldChangeLogList=fieldChangeLogRepository.findByFormName(formName);
        Long createTime=Long.MAX_VALUE;
        Long lastChangeTime= Long.MIN_VALUE;
        for(FieldChangeLog fieldChangeLog:fieldChangeLogList){
            if(fieldChangeLog.getDateTime().getTime()<createTime) createTime=fieldChangeLog.getDateTime().getTime();
            if(fieldChangeLog.getDateTime().getTime()>lastChangeTime) lastChangeTime=fieldChangeLog.getDateTime().getTime();
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        fieldFormInfo.setUserName(fieldClassifyList.getUserName());
        fieldFormInfo.setFormName(fieldClassifyList.getFormName());
        fieldFormInfo.setCreateTime(sdf.format(new Date(createTime)));
        fieldFormInfo.setLastChangeTime(sdf.format(new Date(lastChangeTime)));
        fieldFormInfo.setDescription(fieldClassifyList.getDescription());
        fieldFormInfo.setFather(fieldClassifyList.getFather());
        fieldFormInfo.setUsageCount(String.valueOf(fieldClassifyUsageCount.getCount()));
        return fieldFormInfo;
    }


    @Override
    public List<FieldInfo> getFieldByFromName(String formName) {
        List<FieldClassify> fieldClassifyList=fieldClassifyRepository.findByFormName(formName);
        List<FieldInfo> fieldInfoList=new ArrayList<FieldInfo>();
        for(FieldClassify fieldClassify : fieldClassifyList){
            FieldInfo fieldInfo=new FieldInfo();
            fieldInfo.setFieldName(fieldClassify.getFieldName());
            fieldInfo.setFieldType(fieldClassify.getFieldType());
            fieldInfo.setId(String.valueOf(fieldClassify.getID()));
            fieldInfoList.add(fieldInfo);
        }
        return fieldInfoList;
    }

    @Transactional
    @Override
    public List<String> updateFieldFormInfo(List<FieldInfo> fieldInfoList, String newFormName,String oldFormName,String newDescription,String logDescription){
        List<String> outList=new ArrayList<String>();
        try {

            //判断新表名是否可用
            if(!newFormName.equals(oldFormName)){
                List<String> formNameList=fieldClassifyListRepository.getFormName();
                if(formNameList.contains(newFormName)){
                    outList.add("表名已存在！");
                    return outList;
                }
            }
            //判断字段是否冲突
            Map<String,String> fieldMap=new HashMap<String,String>();
            for(FieldInfo fieldInfo:fieldInfoList){
                String fieldName=fieldInfo.getFieldName()
                        .toLowerCase()
                        .replace(".","")
                        .replace("_","")
                        .replace("-","")
                        .replace("*","");
                if(fieldMap.keySet().contains(fieldName)&&!fieldMap.get(fieldName).equals(fieldInfo.getFieldType()))
                    outList.add("字段"+fieldInfo.getFieldName()+"冲突/r/n");
                else fieldMap.put(fieldName,fieldInfo.getFieldType());
            }
            if(outList.size()!=0) return outList;
            //存fieldChangeLog
            String log="";
            FieldChangeLog fieldChangeLog=new FieldChangeLog();
            fieldChangeLog.setDescription(logDescription);
            fieldChangeLog.setDateTime(new java.sql.Date(new Date().getTime()));
            fieldChangeLog.setFormName(newFormName);
            if(!newFormName.equals(oldFormName))
                log+="重命名："+oldFormName+"-->"+newFormName+"/r/n";
            List<FieldClassify> oldFieldClassify=fieldClassifyRepository.findByFormName(oldFormName);
            Set<String> fieldSet=new HashSet<String>(fieldMap.keySet());
            for(FieldClassify fieldClassify:oldFieldClassify){
                if(!fieldMap.keySet().contains(fieldClassify.getFieldName()))
                    log+="移除字段："+fieldClassify.getFieldName()+"/r/n";
                else if(fieldMap.keySet().contains(fieldClassify.getFieldName())&&!fieldMap.get(fieldClassify.getFieldName()).equals(fieldClassify.getFieldType()))
                    log+="改变字段："+fieldClassify.getFieldName()+" "+fieldClassify.getFieldType()+"-->"+fieldMap.get(fieldClassify.getFieldName())+"/r/n";
                else
                    fieldSet.remove(fieldClassify.getFieldName());
            }
            if(fieldSet.size()!=0){
                log+="增加字段：";
                for(String s:fieldSet)
                    log+=s+"  ";
            }
            fieldChangeLog.setChangeLog(log);
            fieldChangeLogRepository.save(fieldChangeLog);
            List<FieldChangeLog> fieldChangeLogList=fieldChangeLogRepository.findByFormName(oldFormName);
//            fieldChangeLogRepository.deleteByFormName(oldFormName);
            for(FieldChangeLog fieldChangeLog1:fieldChangeLogList)
                fieldChangeLog1.setFormName(newFormName);
            fieldChangeLogRepository.saveAll(fieldChangeLogList);

            //存fieldClassify
            List<FieldClassify> fieldClassifyList=new ArrayList<FieldClassify>();
            for(String s:fieldMap.keySet()){
                FieldClassify fieldClassify=new FieldClassify();
                fieldClassify.setFieldName(s);
                fieldClassify.setFieldType(fieldMap.get(s));
                fieldClassify.setFormName(newFormName);
                fieldClassifyList.add(fieldClassify);
            }
            fieldClassifyRepository.deleteByFormName(oldFormName);
            fieldClassifyRepository.flush();
            fieldClassifyRepository.saveAll(fieldClassifyList);
            //存fieldClassfyList
            FieldClassifyList fieldClassifyList1=fieldClassifyListRepository.findByFormName(oldFormName);
            fieldClassifyList1.setDescription(newDescription);
            fieldClassifyList1.setFormName(newFormName);
            fieldClassifyListRepository.save(fieldClassifyList1);
            //存fieldUsageCount
            FieldClassifyUsageCount fieldClassifyUsageCount=fieldClassifyUsageCountRepository.findByFormName(oldFormName);
//            fieldClassifyUsageCountRepository.deleteByFormName(oldFormName);
            fieldClassifyUsageCount.setFormName(newFormName);
//            fieldClassifyUsageCountRepository.flush();
            fieldClassifyUsageCountRepository.save(fieldClassifyUsageCount);
        }catch (Exception e){
            e.printStackTrace();
            outList.add("更新失败！");
            return outList;
        }
        outList.add("更新成功！");
        return outList;
    }

    @Transactional
    @Override
    public Boolean deleteFormByFormNameAndUserName(String formName,String userName){
        try {
            fieldClassifyListRepository.deleteByFormNameAndUserName(formName,userName);
            fieldClassifyRepository.deleteByFormName(formName);
            fieldChangeLogRepository.deleteByFormName(formName);
            fieldClassifyUsageCountRepository.deleteByFormName(formName);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Map<String,Double> getFieldOverViewByFormName(String formName){
        Map<String,Double> map=new HashMap<String,Double>();
        List<FieldClassify> fieldClassifyList=fieldClassifyRepository.findByFormName(formName);
        Double EI= Double.valueOf(0);
        Double QI_Link= Double.valueOf(0);
        Double QI_Geography= Double.valueOf(0);
        Double QI_DateRecord= Double.valueOf(0);
        Double QI_DateAge= Double.valueOf(0);
        Double QI_Number= Double.valueOf(0);
        Double QI_String= Double.valueOf(0);
        Double SI_Number= Double.valueOf(0);
        Double SI_String= Double.valueOf(0);
        Double UI=Double.valueOf(0);
        Double sum= Double.valueOf(0);
        for(FieldClassify fieldClassify:fieldClassifyList){
            if(fieldClassify.getFieldType().equals("EI")) EI++;
            if(fieldClassify.getFieldType().equals("QI_Link")) QI_Link++;
            if(fieldClassify.getFieldType().equals("QI_Geography")) QI_Geography++;
            if(fieldClassify.getFieldType().equals("QI_DateRecord")) QI_DateRecord++;
            if(fieldClassify.getFieldType().equals("QI_DateAge")) QI_DateAge++;
            if(fieldClassify.getFieldType().equals("QI_Number")) QI_Number++;
            if(fieldClassify.getFieldType().equals("QI_String")) QI_String++;
            if(fieldClassify.getFieldType().equals("SI_Number")) SI_Number++;
            if(fieldClassify.getFieldType().equals("SI_String")) SI_String++;
            if(fieldClassify.getFieldType().equals("UI")) UI++;
        }
        sum=EI+QI_DateAge+QI_DateRecord+QI_Geography+QI_Link+QI_Number+QI_String+SI_Number+SI_String+UI;
        map.put("EI",(Double)(EI/sum));
        map.put("QI_Link",(Double)(QI_Link/sum));
        map.put("QI_Geography",(Double)(QI_Geography/sum));
        map.put("QI_DateRecord",(Double)(QI_DateRecord/sum));
        map.put("QI_DateAge",(Double)(QI_DateAge/sum));
        map.put("QI_Number",(Double)(QI_Number/sum));
        map.put("QI_String",(Double)(QI_String/sum));
        map.put("SI_Number",(Double)(SI_Number/sum));
        map.put("SI_String",(Double)(SI_String/sum));
        map.put("UI",(Double)(UI/sum));
        map.put("SUM",sum);
        return map;
    }

    @Override
    public Map<String,List<String>> getFieldDetailByFormName(String formName){
        Map<String,List<String>> map=new HashMap<String,List<String>>();
        map.put("EI",new ArrayList<String>());
        map.put("QI_Link",new ArrayList<String>());
        map.put("QI_Geography",new ArrayList<String>());
        map.put("QI_DateRecord",new ArrayList<String>());
        map.put("QI_DateAge",new ArrayList<String>());
        map.put("QI_Number",new ArrayList<String>());
        map.put("QI_String",new ArrayList<String>());
        map.put("SI_Number",new ArrayList<String>());
        map.put("SI_String",new ArrayList<String>());
        map.put("UI",new ArrayList<String>());
        List<FieldClassify> fieldClassifyList=fieldClassifyRepository.findByFormName(formName);
        for(FieldClassify fieldClassify:fieldClassifyList){
            if(fieldClassify.getFieldType().equals("EI")) map.get("EI").add(fieldClassify.getFieldName());
            if(fieldClassify.getFieldType().equals("QI_Link")) map.get("QI_Link").add(fieldClassify.getFieldName());
            if(fieldClassify.getFieldType().equals("QI_Geography")) map.get("QI_Geography").add(fieldClassify.getFieldName());
            if(fieldClassify.getFieldType().equals("QI_DateRecord")) map.get("QI_DateRecord").add(fieldClassify.getFieldName());
            if(fieldClassify.getFieldType().equals("QI_DateAge")) map.get("QI_DateAge").add(fieldClassify.getFieldName());
            if(fieldClassify.getFieldType().equals("QI_Number")) map.get("QI_Number").add(fieldClassify.getFieldName());
            if(fieldClassify.getFieldType().equals("QI_String")) map.get("QI_String").add(fieldClassify.getFieldName());
            if(fieldClassify.getFieldType().equals("SI_Number")) map.get("SI_Number").add(fieldClassify.getFieldName());
            if(fieldClassify.getFieldType().equals("SI_String")) map.get("SI_String").add(fieldClassify.getFieldName());
            if(fieldClassify.getFieldType().equals("UI")) map.get("UI").add(fieldClassify.getFieldName());
        }
        return map;
    }


    @Override
    @Transactional
    public List<String> createFrom(String formName,String father,String description){
        List<String> outInfo=new ArrayList<String>();
        String userName="";
        if(formName.trim().equals("")){
            outInfo.add("表名不能为空！");
            return outInfo;
        }
        Set<String> formNameSet=new HashSet<String>(fieldClassifyListRepository.getFormName());
        if(formNameSet.contains(formName)){
            outInfo.add("表名已存在！");
            return outInfo;
        }
        if(!formNameSet.contains(father)){
            outInfo.add("模板不存在！");
            return outInfo;
        }
        try{
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            userName=userDetails.getUsername();
        }catch(Exception e){
            outInfo.add("非法用户名！");
            return  outInfo;
        }
        try{
            //
            FieldClassifyList fieldClassifyList=new FieldClassifyList();
            fieldClassifyList.setFormName(formName);
            fieldClassifyList.setFather(father);
            fieldClassifyList.setDescription(description);
            fieldClassifyList.setUserName(userName);
            fieldClassifyListRepository.save(fieldClassifyList);

            //
            FieldChangeLog fieldChangeLog=new FieldChangeLog();
            fieldChangeLog.setDescription("创建表单");
            fieldChangeLog.setFormName(formName);
            fieldChangeLog.setDateTime(new java.sql.Date(new Date().getTime()));
            fieldChangeLog.setChangeLog("创建表单");
            fieldChangeLogRepository.save(fieldChangeLog);

            //
            FieldClassifyUsageCount fieldClassifyUsageCount=new FieldClassifyUsageCount();
            fieldClassifyUsageCount.setFormName(formName);
            fieldClassifyUsageCount.setCount(0);
            fieldClassifyUsageCountRepository.save(fieldClassifyUsageCount);

            //
            List<FieldClassify> fieldClassifyList1=fieldClassifyRepository.findByFormName(father);
            List<FieldClassify> newFieldClassifyList=new ArrayList<FieldClassify>();
            for(FieldClassify fieldClassify:fieldClassifyList1){
                FieldClassify newfieldClassify=new FieldClassify();
                newfieldClassify.setFormName(formName);
                newfieldClassify.setFieldType(fieldClassify.getFieldType());
                newfieldClassify.setFieldName(fieldClassify.getFieldName());
                newFieldClassifyList.add(newfieldClassify);
            }
            fieldClassifyRepository.saveAll(newFieldClassifyList);

        }catch (Exception e){
            e.printStackTrace();
            outInfo.add("添加失败！");
            return  outInfo;
        }
        outInfo.add("添加成功！");
        return  outInfo;
    }


    @Override
    public Boolean FileToDB() {
//        InputStream is= null;
//        File file=new File(FilePath_mapping+fieldPath);
//        String[] fileList = file.list();
//        for (int i = 0; i < fileList.length; i++) {
//            if(fileList[i].equals("Original.json")) continue;
//            usageFieldClassifyRepository.deleteByFromName(fileList[i].split("\\.")[0]);
//            JSONArray jsonArray=new JSONArray();
//            String path=FilePath_mapping+fieldPath+"/"+fileList[i];
//            try {
//                jsonArray=FileResolve.readerArrayJson(path);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                return false;
//            }
//            for(int j=0;j<jsonArray.size();j++){
//            }
//        }
        return true;
    }

    @Override
    public Boolean DBToFile() {
//        List<String> fieldNameList=usageFieldClassifyRepository.getFromName();
//        for(String fieldName:fieldNameList) {
//            List<UsageFieldClassify> usageFieldClassifyList = usageFieldClassifyRepository.findByFromName(fieldName);
//            JSONArray jsonArray=new JSONArray();
//            for(UsageFieldClassify usageFieldClassify:usageFieldClassifyList){
//                JSONObject jsonObject=new JSONObject();
//                jsonObject.put("fieldName",usageFieldClassify.getFieldName());
//                jsonObject.put("fieldType",usageFieldClassify.getFieldType());
//                jsonArray.add(jsonObject);
//            }
//            String jsonStr =jsonArray.toJSONString();
//            PrintWriter pw = null;
//            try {
//                pw = new PrintWriter(new BufferedWriter(new FileWriter(FilePath_mapping+fieldPath+"/backup"+fieldName+".json")));
//            } catch (IOException e) {
//                e.printStackTrace();
//                return false;
//            }
//            pw.print(jsonStr);
//            pw.flush();
//            pw.close();
//        }
        return true;
    }


}
