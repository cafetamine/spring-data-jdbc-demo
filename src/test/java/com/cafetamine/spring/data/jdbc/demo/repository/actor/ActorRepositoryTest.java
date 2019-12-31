package com.cafetamine.spring.data.jdbc.demo.repository.actor;

import com.cafetamine.spring.data.jdbc.demo.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.domain.actor.IActorRepository;

import com.cafetamine.spring.data.jdbc.demo.domain.def.Gender;
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

    private static final JdbcActorRepository jdbcActorRepository = mock(JdbcActorRepository.class);

    private static final IActorRepository actorRepository = new ActorRepository(jdbcActorRepository);


    private ActorEntity phoenixEntity, niroEntity;
    private Actor phoenix, niro;


    @BeforeEach
    void beforeEach() {
        phoenixEntity = new ActorEntity(1L, "Joaquin", "Phoenix", LocalDate.of(1974, 10, 28), null, Gender.Male);
        niroEntity = new ActorEntity(2L, "Robert", "De Niro", LocalDate.of(1943, 8, 17), null, Gender.Male);
        phoenix = new Actor(1L, "Joaquin", "Phoenix", LocalDate.of(1974, 10, 28), null, Gender.Male);
        niro = new Actor(2L, "Robert", "De Niro", LocalDate.of(1943, 8, 17), null, Gender.Male);
    }

    @Test
    void create() {
        when(jdbcActorRepository.save(phoenixEntity.withId(null))).thenReturn(phoenixEntity);

        assertThat(actorRepository.create(phoenix.withId(null))).isEqualTo(phoenix);
    }

    @Test
    void findAll_returnsEmptyList() {
        when(jdbcActorRepository.findAll()).thenReturn(Collections.emptyList());

        assertThat(actorRepository.findAll()).isEqualTo(Collections.emptyList());
    }

    @Test
    void findAll_returnsExpectedValues() {
        when(jdbcActorRepository.findAll()).thenReturn(Arrays.asList(phoenixEntity, niroEntity));

        assertThat(actorRepository.findAll()).isEqualTo(Arrays.asList(phoenix, niro));
    }


    @Test
    void findById_NonExisting() {
        when(jdbcActorRepository.findById(100L)).thenReturn(Optional.empty());

        assertThat(actorRepository.findById(100L)).isEmpty();
    }

    @Test
    void findById() {
        when(jdbcActorRepository.findById(1L)).thenReturn(Optional.of(phoenixEntity));

        assertThat(actorRepository.findById(1L)).hasValue(phoenix);
    }

    @Test
    void findByFullname() {
        when(jdbcActorRepository.findByFullname("Joaquin Phoenix")).thenReturn(Optional.of(phoenixEntity));

        assertThat(actorRepository.findByFullname("Joaquin Phoenix")).hasValue(phoenix);
    }

    @Test
    void updateDeathdate() {
        when(jdbcActorRepository.updateDeathdate(1L, LocalDate.of(2019, 12, 10))).thenReturn(true);
        when(jdbcActorRepository.findById(1L)).thenReturn(Optional.of(phoenixEntity));

        assertThat(actorRepository.updateDeathdate(1L, LocalDate.of(2019, 12, 10))).hasValue(phoenix);
    }

    @Test
    void findAllByGender() {
        when(jdbcActorRepository.findAllByGender(Gender.Male.name())).thenReturn(Arrays.asList(phoenixEntity, niroEntity));

        assertThat(actorRepository.findAllByGender(Gender.Male)).isEqualTo(Arrays.asList(phoenix, niro));
    }

    @Test
    void findAllByGender_ForNonMatching() {
        when(jdbcActorRepository.findAllByGender(Gender.Male.name())).thenReturn(Collections.emptyList());

        assertThat(actorRepository.findAllByGender(Gender.Male)).isEqualTo(Collections.emptyList());
    }

}