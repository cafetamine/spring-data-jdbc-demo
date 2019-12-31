package com.cafetamine.spring.data.jdbc.demo.repository.movie;

import com.cafetamine.spring.data.jdbc.demo.config.JdbcNamingStrategyConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@DataJdbcTest
class JdbcMovieRepositoryTest {

    @Sql("data.sql")
    @Configuration @EnableAutoConfiguration
    static class JdbcTestConfiguration {

        @Bean
        NamingStrategy namingStrategy(){
            return new JdbcNamingStrategyConfig();
        }

    }

    @Autowired JdbcMovieRepository repository;

    private MovieActorsReference phoenixRef, niroRef;
    private Map<String, MovieActorsReference> actors;

    private MovieGenresReference crimeRef, thrillerRef;
    private List<MovieGenresReference> genres;

    private MovieAggregate movieAggregate;

    @BeforeEach
    void beforeEach() {
        phoenixRef = new MovieActorsReference(1L);
        niroRef = new MovieActorsReference(2L);
        actors = new HashMap<>() {{
            put("Arthur Fleck", phoenixRef);
            put("Murray Franklin", niroRef);
        }};

        crimeRef = new MovieGenresReference(1);
        thrillerRef = new MovieGenresReference(2);
        genres = Arrays.asList(crimeRef, thrillerRef);

        movieAggregate = repository.save(new MovieAggregate(null, "Jocker", 122L, LocalDate.of(2019, 10, 4), actors, genres));
    }

    @Test
    void findById() {
        assertThat(repository.findById(movieAggregate.getId())).hasValue(movieAggregate);
    }

    @Test
    void findById_NonExisting() {
        assertThat(repository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void findByGenreId() {
        assertThat(repository.findByGenreId(1)).isEqualTo(new LinkedHashSet<MovieAggregate>() {{ add(movieAggregate); }});
    }

    @Test
    void findByActorId() {
        assertThat(repository.findByActorId(1L)).isEqualTo(new LinkedHashSet<MovieAggregate>() {{ add(movieAggregate); }});
    }

}
