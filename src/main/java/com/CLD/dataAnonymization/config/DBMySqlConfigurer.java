package com.CLD.dataAnonymization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 该类用于配置MySql数据库
 * @Author CLD
 * @Date 2018/4/27 20:40
 **/
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        transactionManagerRef = "transactionManagerMySql",
        entityManagerFactoryRef = "entityManagerFactoryMySql",
        basePackages = {"com.CLD.dataAnonymization.dao.mysql"})
public class DBMySqlConfigurer {

    @Autowired
    @Qualifier("mySqlDataSource")
    private DataSource mySqlDataSource;

    @Autowired
    private JpaProperties jpaProperties;

    private Map<String, Object> getVendorProperties() {
        return jpaProperties.getHibernateProperties(new HibernateSettings());
    }

    @Bean(name = "entityManagerFactoryMySql")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryMySql(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(mySqlDataSource).properties(getVendorProperties())
                .packages("com.CLD.dataAnonymization.dao.mysql").persistenceUnit("MySqlPersistenceUnit")
                .build();
    }

    @Bean(name = "entityManagerMySql")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryMySql(builder).getObject().createEntityManager();
    }

    @Bean(name = "transactionManagerSecondary")
    public PlatformTransactionManager transactionManagerMySql(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryMySql(builder).getObject());
    }
}
