package com.CLD.dataAnonymization.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据源配置
 * @Author CLD
 * @Date 2018/4/27 19:23
 **/
@Configuration
public class DataSourceConfigurer {

    @Bean(name = "h2DataSource")
    @Qualifier("h2DataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.h2")
    public DataSource h2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mySqlDataSource")
    @Qualifier("mySqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource mySqlDataSource() {
        return DataSourceBuilder.create().build();
    }
}
