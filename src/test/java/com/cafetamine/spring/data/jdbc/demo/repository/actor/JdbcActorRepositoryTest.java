package com.cafetamine.spring.data.jdbc.demo.repository.actor;

import com.cafetamine.spring.data.jdbc.demo.config.JdbcNamingStrategyConfig;
import com.cafetamine.spring.data.jdbc.demo.domain.def.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.NamingStrategy;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


@DataJdbcTest
class JdbcActorRepositoryTest {

    @Configuration @EnableAutoConfiguration
    static class JdbcTestConfiguration {

        @Bean NamingStrategy namingStrategy(){
            return new JdbcNamingStrategyConfig();
        }

    }

    @Autowired JdbcActorRepository repository;

    private ActorEntity expected;

    @BeforeEach
    void beforeEach() {
        expected = repository.save(new ActorEntity(
                null,
                "Joaquin",
                "Phoenix",
                LocalDate.of(1974, 10, 28),
                null,
                Gender.Male
        ));
    }

    @Test
    void test_M_findById() {
        assertThat(repository.findById(expected.getId())).hasValue(expected);
    }

    @Test
    void test_M_findById_NonExisting() {
        assertThat(repository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void test_M_findByFullname() {
        assertThat(repository.findByFullname("Joaquin Phoenix")).hasValue(expected);
    }

    @Test
    void test_M_updateDeathdate() {
        final LocalDate expectedDate = LocalDate.of(2019, 12, 9);
        final boolean isSuccess = repository.updateDeathdate(expected.getId(), expectedDate); // sooorryy

        assertThat(isSuccess).isTrue();
        assertThat(repository.findById(expected.getId())).hasValueSatisfying(actor ->
                assertThat(actor.getDeathdate()).isEqualTo(expectedDate)
        );
    }

    @Test
    void test_M_findAllByGender() {
        assertThat(repository.findAllByGender(Gender.Male.name())).isEqualTo(Collections.singletonList(expected));
    }

    @Test
    void test_M_findAllByGender_ForNonMatching() {
        assertThat(repository.findAllByGender(Gender.Female.name())).isEqualTo(Collections.emptyList());
    }

}