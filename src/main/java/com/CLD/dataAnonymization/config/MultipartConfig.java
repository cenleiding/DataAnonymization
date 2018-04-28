package com.CLD.dataAnonymization.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.MultipartConfigElement;

/**
 * 修改文件上传限制
 * @Author CLD
 * @Date 2018/4/19 16:59
 **/
@Component
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("2048MB");
        factory.setMaxRequestSize("2048MB");
        return factory.createMultipartConfig();
    }
}
