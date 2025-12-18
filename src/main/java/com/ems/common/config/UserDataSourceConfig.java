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
        basePackages = "com.ems.user.repositories",
        entityManagerFactoryRef = "userEntityManagerFactory",
        transactionManagerRef = "userTransactionManager"
)
public class UserDataSourceConfig {

  @Bean
  @ConfigurationProperties("user.datasource")
  public DataSourceProperties userDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "userDataSource")
  @ConfigurationProperties("user.datasource")
  public DataSource userDataSource(
          @Qualifier("userDataSourceProperties") DataSourceProperties props
  ) {
    return props
            .initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
  }

  private Map<String, Object> userJpaProperties() {
    Map<String, Object> props = new HashMap<>();
    props.put("hibernate.hbm2ddl.auto", "update");
    props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    return props;
  }

  @Bean(name = "userEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(
          EntityManagerFactoryBuilder builder,
          @Qualifier("userDataSource") DataSource dataSource
  ) {
    return builder
            .dataSource(dataSource)
            .packages("com.ems.user.entities")
            .properties(userJpaProperties())
            .persistenceUnit("user")
            .build();
  }

  @Bean(name = "userTransactionManager")
  public PlatformTransactionManager userTransactionManager(
          @Qualifier("userEntityManagerFactory") LocalContainerEntityManagerFactoryBean emf
  ) {
    return new JpaTransactionManager(emf.getObject());
  }
}
