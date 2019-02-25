package com.CLD.dataAnonymization.service.regularAndDictionary.dictionary;

import com.CLD.dataAnonymization.dao.h2.entity.Dictionary;
import com.CLD.dataAnonymization.dao.h2.repository.DictionaryrRepository;
import com.CLD.dataAnonymization.dao.h2.repository.RegularLibRepository;
import com.CLD.dataAnonymization.util.deidentifier.io.FileResolve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Description:
 * @Author CLD
 * @Date 2019/2/22 14:32
 */
@Service
public class DictionaryServiceImpl implements DictionaryService {

    @Value("${package.jar.name}")
    private String jarName;

    @Autowired
    RegularLibRepository regularLibRepository;

    @Autowired
    DictionaryrRepository dictionaryrRepository;

    @Override
    public String uploadDictionaryFile(MultipartHttpServletRequest re) throws IOException {

        // 获取规则库名
        Map<String,String[]> parameterMap=re.getParameterMap();
        String libName = parameterMap.get("libName")[0];
        String description = parameterMap.get("description")[0];

        //获取已有文件名，防止相同文件名
        List<String> libNameList = dictionaryrRepository.getFileNameByLibName(libName);

        // 存储文件
        MultiValueMap<String, MultipartFile> files=re.getMultiFileMap();
        for (String key:files.keySet()){
            MultipartFile file = files.getFirst(key);
            InputStream is = file.getInputStream();
            ArrayList<ArrayList<String>> csv = FileResolve.readerCsv(is);
            String content = "";
            for (int i =0;i<csv.size();i++)
                for(int j=0;j<csv.get(i).size();j++)
                    if((csv.get(i).get(j)!=null)&&(csv.get(i).get(j).replace(" ","")!=""))
                        content+=csv.get(i).get(j)+',';

            String filename = file.getOriginalFilename();
            if (libNameList.contains(filename)) return "fileName_repeat";
            Dictionary dictionary = new Dictionary();
            dictionary.setLibName(libName);
            dictionary.setContent(content);
            dictionary.setFileName(filename);
            dictionary.setDescription(description);
            dictionary.setUploadTime(new java.sql.Date(new Date().getTime()));
            dictionaryrRepository.save(dictionary);
        }

        return "upload_success";
    }

    @Override
    public Boolean initDictionary(String fileName, String libName, String description,String Path) {

        ArrayList<ArrayList<String>> csv = null;
        String content = "";
        try {
            InputStream is=new Object(){
                public InputStream get(){
                    return this.getClass().getClassLoader().getResourceAsStream(Path);
                }
            }.get();
            csv =  FileResolve.readerCsv(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i =0;i<csv.size();i++)
            for(int j=0;j<csv.get(i).size();j++)
                if((csv.get(i).get(j)!=null)&&(csv.get(i).get(j).replace(" ","")!=""))
                    content+=csv.get(i).get(j)+',';

        Dictionary dictionary = new Dictionary();
        dictionary.setDescription(description);
        dictionary.setFileName(fileName);
        dictionary.setLibName(libName);
        dictionary.setUploadTime(new java.sql.Date(new java.util.Date().getTime()));
        dictionary.setContent(content);
        dictionaryrRepository.saveAndFlush(dictionary);

        return true;
    }

    @Transactional
    @Override
    public Boolean deleteLib(String libName) {
        dictionaryrRepository.deleteByLibName(libName);
        dictionaryrRepository.flush();
        return true;
    }

    @Override
    public Boolean deleteDictionary(String fileName, String libName) {
        dictionaryrRepository.deleteByLibNameAndFileName(libName,fileName);
        return true;
    }

    @Override
    public String updateLibName(String old_libName, String new_libName) {
        List<Dictionary> dictionaryList = dictionaryrRepository.findByLibName(old_libName);
        for (Dictionary dictionary : dictionaryList){
            dictionary.setLibName(new_libName);
        }
        return null;
    }

    @Override
    public List<Dictionary> getDictionaryByLibName(String libName) {
        return dictionaryrRepository.findByLibName(libName);
    }

    @Override
    public String downloadDictionary(HttpServletRequest rq,String fileName, String libName) throws UnsupportedEncodingException {
        Dictionary dictionary = dictionaryrRepository.findByLibNameAndFileName(libName,fileName);
        String savePath =rq.getSession().getServletContext().getRealPath("/"+libName);
        FileResolve.createFile(savePath);

        ArrayList<ArrayList<String>> csv = new ArrayList<ArrayList<String>>();
        ArrayList<String> line = new ArrayList<String>();
        String[] ds = dictionary.getContent().split(",");
        for (String s : ds)
            line.add(s);
        csv.add(line);
        FileResolve.writerCsv(savePath+"/"+fileName,csv);
        return "/"+libName+"/"+fileName;
    }
}
