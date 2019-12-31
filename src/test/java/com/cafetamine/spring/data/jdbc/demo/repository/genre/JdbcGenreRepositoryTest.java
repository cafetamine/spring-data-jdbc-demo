package com.cafetamine.spring.data.jdbc.demo.repository.genre;

import com.cafetamine.spring.data.jdbc.demo.config.JdbcNamingStrategyConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.data.relational.core.mapping.NamingStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;


@DataJdbcTest
class JdbcGenreRepositoryTest {

    @Configuration @EnableAutoConfiguration
    static class JdbcTestConfiguration {

        @Bean
        NamingStrategy namingStrategy(){
            return new JdbcNamingStrategyConfig();
        }

    }

    @Autowired JdbcGenreRepository repository;

    private GenreEntity comedy, tragedy;


    @BeforeEach
    void beforeEach() {
        comedy = repository.save(new GenreEntity(null,"comedy"));
        tragedy = repository.save(new GenreEntity(null,"tragedy"));
    }

    @Test
    void save_newEntity() {
        final GenreEntity saved = repository.save(new GenreEntity(null, "thriller"));

        assertThat(repository.count()).isEqualTo(3L);
        assertThat(repository.findByName("thriller")).hasValue(saved);
    }

    @Test
    void save_existingEntity() {
        assertThat(catchThrowableOfType(() ->
                repository.save(new GenreEntity(null, "comedy")),
                DbActionExecutionException.class
        )).isNotNull();
        assertThat(repository.count()).isEqualTo(2L);
    }

    @Test
    void findByName_NonExisting() {
        assertThat(repository.findByName("does not exist")).isEmpty();
    }

    @Test
    void findByName() {
        assertThat(repository.findByName("comedy")).hasValue(comedy);
        assertThat(repository.findByName("tragedy")).hasValue(tragedy);
    }


}