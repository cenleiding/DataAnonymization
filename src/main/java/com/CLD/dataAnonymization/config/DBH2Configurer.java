package com.CLD.dataAnonymization.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 用于配置H2数据库
 * @Author CLD
 * @Date 2018/4/27 19:30
 **/

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryH2",
        transactionManagerRef = "transactionManagerH2",
        basePackages = {"com.CLD.dataAnonymization.dao.h2"})
public class DBH2Configurer {
    @Autowired
    @Qualifier("h2DataSource")
    private DataSource h2DataSource;

    @Autowired
    private JpaProperties jpaProperties;

    private Map<String, Object> getVendorProperties() {
        return jpaProperties.getHibernateProperties(new HibernateSettings());
    }

    @Primary
    @Bean(name = "entityManagerFactoryH2")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryH2(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(h2DataSource)
                .properties(getVendorProperties())
                .packages("com.CLD.dataAnonymization.dao.h2")
                .persistenceUnit("h2PersistenceUnit")
                .build();
    }

    @Primary
    @Bean(name = "entityManagerH2")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryH2(builder).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "transactionManagerH2")
    public PlatformTransactionManager transactionManagerH2(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryH2(builder).getObject());
    }
}
