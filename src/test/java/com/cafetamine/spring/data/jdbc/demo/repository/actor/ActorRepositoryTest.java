package com.cafetamine.spring.data.jdbc.demo.repository.actor;

import com.cafetamine.spring.data.jdbc.demo.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.domain.actor.IActorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
class ActorRepositoryTest {

    private final static JdbcActorRepository jdbcActorRepository = mock(JdbcActorRepository.class);

    private final static IActorRepository actorRepository = new ActorRepository(jdbcActorRepository);


    private ActorEntity phoenixEntity, niroEntity;
    private Actor phoenix, niro;


    @BeforeEach
    void beforeEach() {
        phoenixEntity = new ActorEntity(1L, "Joaquin", "Phoenix", LocalDate.of(1974, 10, 28), null);
        niroEntity = new ActorEntity(2L, "Robert", "De Niro", LocalDate.of(1943, 8, 17), null);
        phoenix = new Actor(1L, "Joaquin", "Phoenix", LocalDate.of(1974, 10, 28), null);
        niro = new Actor(2L, "Robert", "De Niro", LocalDate.of(1943, 8, 17), null);
    }

    @Test
    void test_M_findAll_returnsEmptyList() {
        when(jdbcActorRepository.findAll()).thenReturn(Collections.emptyList());

        assertThat(actorRepository.findAll()).isEqualTo(Collections.emptyList());
    }

    @Test
    void test_M_findAll_returnsExpectedValues() {
        when(jdbcActorRepository.findAll()).thenReturn(Arrays.asList(phoenixEntity, niroEntity));

        assertThat(actorRepository.findAll()).isEqualTo(Arrays.asList(phoenix, niro));
    }


    @Test
    void test_M_findById_NonExisting() {
        when(jdbcActorRepository.findById(100L)).thenReturn(Optional.empty());

        assertThat(actorRepository.findById(100L)).isEmpty();
    }

    @Test
    void test_M_findById() {
        when(jdbcActorRepository.findById(1L)).thenReturn(Optional.of(phoenixEntity));

        assertThat(actorRepository.findById(1L)).hasValue(phoenix);
    }

    @Test
    void test_M_findByFullname() {
        when(jdbcActorRepository.findByFullname("Joaquin Phoenix")).thenReturn(Optional.of(phoenixEntity));

        assertThat(actorRepository.findByFullname("Joaquin Phoenix")).hasValue(phoenix);
    }

    @Test
    void test_M_updateDeathdate() {
        when(jdbcActorRepository.updateDeathdate(1L, LocalDate.of(2019, 12, 10))).thenReturn(true);
        when(jdbcActorRepository.findById(1L)).thenReturn(Optional.of(phoenixEntity));

        assertThat(actorRepository.updateDeathdate(1L, LocalDate.of(2019, 12, 10))).hasValue(phoenix);
    }

}