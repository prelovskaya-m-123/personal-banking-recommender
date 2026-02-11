package org.personal.banking.recommender.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class JdbcConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(
            @Qualifier("h2ReadOnlyDataSource") DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }

}
