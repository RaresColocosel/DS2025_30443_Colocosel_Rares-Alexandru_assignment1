package com.ems.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;        // <-- add this
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.ems.auth.repositories",
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager"
)
public class AuthDataSourceConfig {

  @Bean
  @ConfigurationProperties("auth.datasource")
  public DataSourceProperties authDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "authDataSource")
  @ConfigurationProperties("auth.datasource")
  public DataSource authDataSource(
          @Qualifier("authDataSourceProperties") DataSourceProperties props
  ) {
    return props
            .initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
  }

  private Map<String, Object> authJpaProperties() {
    Map<String, Object> props = new HashMap<>();
    props.put("hibernate.hbm2ddl.auto", "update");
    props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    return props;
  }

  @Bean(name = "authEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(
          EntityManagerFactoryBuilder builder,
          @Qualifier("authDataSource") DataSource dataSource
  ) {
    return builder
            .dataSource(dataSource)
            .packages("com.ems.auth.entities")
            .properties(authJpaProperties())
            .persistenceUnit("auth")
            .build();
  }

  @Bean(name = "authTransactionManager")
  @Primary   // <-- THIS makes it the default TransactionManager
  public PlatformTransactionManager authTransactionManager(
          @Qualifier("authEntityManagerFactory") LocalContainerEntityManagerFactoryBean emf
  ) {
    return new JpaTransactionManager(emf.getObject());
  }
}
