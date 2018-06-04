package com.CLD.dataAnonymization.service;

import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @Author CLD
 * @Date 2018/6/3 13:02
 **/
@Service
public class ResourcesFileInitializeServiceImpl implements ResourcesFileInitializeService{

    @Override
    public Boolean InitializeResourcesFile() {

        File file=null;
        System.out.println("资源初始化开始...");

        String outPath= new Object(){
            public String get(){
                return this.getClass().getClassLoader().getResource("").getPath();
            }
        }.get().replaceAll("target/classes/","")
                .replaceAll("1.jar!/BOOT-INF/classes!/","")
                .replaceAll("file:","")+"resources";
        System.out.println(outPath);
        file=new File(outPath+"/openEhr");
        if(!file.exists()){
            System.out.println(file.mkdirs());
        }
        file=new File(outPath+"/expand");
        if(!file.exists()){
            System.out.println(file.mkdirs());
        }

        //复制原型节点分类表
        copyFile("com/CLD/dataAnonymization/Resources/openEhr/archetypeNodeClassify.json",outPath+"/openEhr/archetypeNodeClassify.json");

        //复制其他拓展表
        String exPath= new Object(){
            public String get(){
                return this.getClass().getClassLoader().getResource("com/CLD/dataAnonymization/Resources/expand").getPath();
            }
        }.get();
        file=new File(exPath);
        String[] fileList = file.list();
        for (int i = 0; i < fileList.length; i++) {
            copyFile("com/CLD/dataAnonymization/Resources/expand/"+fileList[i],outPath+"/expand/"+fileList[i]);
        }

        System.out.println("资源初始化成功！");

        return true;
    }

    private void copyFile(String inPath,String outPath){
        File file=new File(outPath);
        if(!file.exists()){
            InputStream in=new Object(){
                public InputStream get(){
                    return this.getClass().getClassLoader().getResourceAsStream(inPath);
                }
            }.get();
            try {
                FileOutputStream out = new FileOutputStream(file);
                byte buffer[] = new byte[4*1024];
                int len = 0;
                while((len=in.read(buffer))>0){
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
