package org.personal.banking.recommender.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class RecommendationsDataSourceConfiguration {

    /**
     * DataSource для H2 (только чтение).
     */
    @Bean(name = "h2ReadOnlyDataSource")
    public DataSource h2ReadOnlyDataSource(
            @Value("${application.recommendations-db.url}") String recommendationsUrl) {
        var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(recommendationsUrl);
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setReadOnly(true);
        dataSource.setMaximumPoolSize(5);
        dataSource.setConnectionTimeout(30000);
        return dataSource;
    }

    /**
     * JdbcTemplate для работы с H2.
     */
    @Bean(name = "recommendationsJdbcTemplate")
    public JdbcTemplate recommendationsJdbcTemplate(
            @Qualifier("h2ReadOnlyDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
