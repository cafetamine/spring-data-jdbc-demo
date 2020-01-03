package com.cafetamine.spring.data.jdbc.demo;

import com.cafetamine.spring.data.jdbc.demo.config.JdbcNamingStrategyConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.relational.core.mapping.NamingStrategy;


@TestConfiguration
public class SpringDataJdbcDemoApplicationTestsConfiguration {

    static class JdbcTestConfiguration {

        @Bean
        NamingStrategy namingStrategy(){
            return new JdbcNamingStrategyConfig();
        }

    }

}
