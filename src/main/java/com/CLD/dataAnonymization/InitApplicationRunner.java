package com.CLD.dataAnonymization;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.*;


/**
 * 启动运行初始化
 * @Author CLD
 * @Date 2018/4/26 14:57
 **/
@Component
@Order(value = 1)
public class InitApplicationRunner implements ApplicationRunner{

    @Override
    public void run(ApplicationArguments args) throws Exception {

        System.out.println("资源初始化开始...");

        String path= new Object(){
            public String get(){
                return this.getClass().getClassLoader().getResource("").getPath();
            }
        }.get().replaceAll("target/classes/","")
                .replaceAll("1.jar!/BOOT-INF/classes!/","")
                .replaceAll("file:","")+"resources";
        System.out.println(path);
        File file=new File(path);
        if(!file.exists()){
            System.out.println(file.mkdirs());
        }

        String pathm=path+"/Form_mapping.json";
        file=new File(pathm);
        if(!file.exists()){
            InputStream in=new Object(){
                public InputStream get(){
                    return this.getClass().getClassLoader().getResourceAsStream("com/CLD/dataAnonymization/util/deidentifier/Resources/Form_mapping.json");
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

        String patha=path+"/Address.json";
        file=new File(patha);
        if(!file.exists()){
            InputStream in=new Object(){
                public InputStream get(){
                    return this.getClass().getClassLoader().getResourceAsStream("com/CLD/dataAnonymization/util/deidentifier/Resources/Address.json");
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

        System.out.println("资源初始化成功！");
    }
}
