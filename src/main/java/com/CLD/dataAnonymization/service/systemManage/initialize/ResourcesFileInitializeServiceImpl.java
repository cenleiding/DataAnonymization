package com.CLD.dataAnonymization.service.systemManage.initialize;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * 改类用于系统启动时的文件初始化
 * @Author CLD
 * @Date 2018/6/3 13:02
 **/
@Service
public class ResourcesFileInitializeServiceImpl implements ResourcesFileInitializeService{

    @Value("${node.out.archetype.path}")
    private String outArchetypePath;

    @Value("${node.in.archetype.path}")
    private String inArchetypePath;

    @Value("${node.out.expand.path}")
    private String outExpandPath;

    @Value("${node.in.expand.path}")
    private String inExpandPath;

    @Value("${node.archetype.name}")
    private String ArchetypeName;

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
                .replaceAll("file:","");

        file=new File(outPath+outArchetypePath);
        if(!file.exists()){
            System.out.println(file.mkdirs());
        }
        file=new File(outPath+outExpandPath);
        if(!file.exists()){
            System.out.println(file.mkdirs());
        }

        //复制原型节点分类表
        copyFile(inArchetypePath+"/"+ArchetypeName,outPath+outArchetypePath+"/"+ArchetypeName);

        //复制其他拓展表
        String exPath= new Object(){
            public String get(){
                return this.getClass().getClassLoader().getResource(inExpandPath).getPath();
            }
        }.get();
        file=new File(exPath);
        String[] fileList = file.list();
        for (int i = 0; i < fileList.length; i++) {
            copyFile(inExpandPath+fileList[i],outPath+outExpandPath+"/"+fileList[i]);
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
