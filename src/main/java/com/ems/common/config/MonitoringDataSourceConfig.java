package com.ems.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.ems.monitoring.repositories",
        entityManagerFactoryRef = "monitoringEntityManagerFactory",
        transactionManagerRef = "monitoringTransactionManager"
)
public class MonitoringDataSourceConfig {

    @Bean
    @ConfigurationProperties("monitoring.datasource")
    public DataSourceProperties monitoringDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "monitoringDataSource")
    @ConfigurationProperties("monitoring.datasource")
    public DataSource monitoringDataSource(
            @Qualifier("monitoringDataSourceProperties") DataSourceProperties props
    ) {
        return props
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    private Map<String, Object> monitoringJpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        return props;
    }

    @Bean(name = "monitoringEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean monitoringEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("monitoringDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.ems.monitoring.entities") // Strict scanning
                .properties(monitoringJpaProperties())
                .persistenceUnit("hourlyconsumption")
                .build();
    }

    @Bean(name = "monitoringTransactionManager")
    public PlatformTransactionManager monitoringTransactionManager(
            @Qualifier("monitoringEntityManagerFactory") LocalContainerEntityManagerFactoryBean emf
    ) {
        return new JpaTransactionManager(emf.getObject());
    }
}