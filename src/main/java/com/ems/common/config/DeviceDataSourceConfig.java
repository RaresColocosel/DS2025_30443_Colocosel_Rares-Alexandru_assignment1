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
        basePackages = "com.ems.device.repositories",
        entityManagerFactoryRef = "deviceEntityManagerFactory",
        transactionManagerRef = "deviceTransactionManager"
)
public class DeviceDataSourceConfig {

  @Bean
  @ConfigurationProperties("device.datasource")
  public DataSourceProperties deviceDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "deviceDataSource")
  @ConfigurationProperties("device.datasource")
  public DataSource deviceDataSource(
          @Qualifier("deviceDataSourceProperties") DataSourceProperties props
  ) {
    return props
            .initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
  }

  private Map<String, Object> deviceJpaProperties() {
    Map<String, Object> props = new HashMap<>();
    props.put("hibernate.hbm2ddl.auto", "update");
    props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    return props;
  }

  @Bean(name = "deviceEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean deviceEntityManagerFactory(
          EntityManagerFactoryBuilder builder,
          @Qualifier("deviceDataSource") DataSource dataSource
  ) {
    return builder
            .dataSource(dataSource)
            .packages("com.ems.device.entities")
            .properties(deviceJpaProperties())
            .persistenceUnit("device")
            .build();
  }

  @Bean(name = "deviceTransactionManager")
  public PlatformTransactionManager deviceTransactionManager(
          @Qualifier("deviceEntityManagerFactory") LocalContainerEntityManagerFactoryBean emf
  ) {
    return new JpaTransactionManager(emf.getObject());
  }
}