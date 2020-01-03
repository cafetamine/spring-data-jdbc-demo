package com.cafetamine.spring.data.jdbc.demo.repository.actor;

import com.cafetamine.spring.data.jdbc.demo.core.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.core.application.actor.IActorRepository;

import com.cafetamine.spring.data.jdbc.demo.core.domain.def.Gender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
class ActorRepositoryTest {

    private static final JdbcActorRepository jdbcActorRepository = mock(JdbcActorRepository.class);
    private static final IActorRepository actorRepository = new ActorRepository(jdbcActorRepository);


    private static final ActorEntity phoenixEntity = new ActorEntity(1L, "Joaquin", "Phoenix", LocalDate.of(1974, 10, 28), null, Gender.Male);
    private static final ActorEntity deNiroEntity = new ActorEntity(2L, "Robert", "De Niro", LocalDate.of(1943, 8, 17), null, Gender.Male);
    private static final ActorEntity beniginiEntity = new ActorEntity(3L, "Roberto", "Benigini", LocalDate.of(1952, 10, 27), null, Gender.Male);
    private static final ActorEntity bonacelliEntity = new ActorEntity(4L, "Paolo", "Bonacelli", LocalDate.of(1937, 2, 28), null, Gender.Male);
    private static final ActorEntity knightleyEntity = new ActorEntity(5L, "Keira", "Knightley", LocalDate.of(1985, 4, 26), null, Gender.Female);

    private static final Actor phoenixDomain = new Actor(1L, "Joaquin", "Phoenix", LocalDate.of(1974, 10, 28), null, Gender.Male);
    private static final Actor deNiroDomain = new Actor(2L, "Robert", "De Niro", LocalDate.of(1943, 8, 17), null, Gender.Male);
    private static final Actor beniginiDomain = new Actor(3L, "Roberto", "Benigini", LocalDate.of(1952, 10, 27), null, Gender.Male);
    private static final Actor bonacelliDomain = new Actor(4L, "Paolo", "Bonacelli", LocalDate.of(1937, 2, 28), null, Gender.Male);
    private static final Actor knightleyDomain = new Actor(5L, "Keira", "Knightley", LocalDate.of(1985, 4, 26), null, Gender.Female);


    @ParameterizedTest @MethodSource("dataSetCreate")
    void create(final Actor domain, final ActorEntity entity) {
        when(jdbcActorRepository.save(entity.withId(null))).thenReturn(entity);

        assertThat(actorRepository.create(domain.withId(null))).isEqualTo(domain);
    }

    @ParameterizedTest @MethodSource("dataSetCreateAll")
    void createAll(final List<Actor> domain, final Collection<ActorEntity> entity) {
        when(jdbcActorRepository.saveAll(entity)).thenReturn(entity);

        assertThat(actorRepository.createAll(domain)).containsExactlyElementsOf(domain);
    }

    @ParameterizedTest @MethodSource("dataSetFindAll")
    void findAll(final Collection<Actor> domain, final Collection<ActorEntity> entity) {
        when(jdbcActorRepository.findAll()).thenReturn(entity);

        assertThat(actorRepository.findAll()).containsExactlyElementsOf(domain);
    }

    @ParameterizedTest @MethodSource("dataSetFindById")
    void findById(final Long id, final Actor domain, final ActorEntity entity) {
        when(jdbcActorRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThat(actorRepository.findById(id)).hasValue(domain);
    }

    @Test
    void findById_NonExisting() {
        when(jdbcActorRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThat(actorRepository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @ParameterizedTest @MethodSource("dataSetFindByFullname")
    void findByFullname(final String fullname, final Actor domain, final ActorEntity entity) {
        when(jdbcActorRepository.findByFullname(fullname)).thenReturn(Optional.of(entity));

        assertThat(actorRepository.findByFullname(fullname)).hasValue(domain);
    }

    @Test
    void findByFullname_NonExisting() {
        when(jdbcActorRepository.findByFullname("Non Existing")).thenReturn(Optional.empty());

        assertThat(actorRepository.findByFullname("Non Existing")).isEmpty();
    }

    @ParameterizedTest @MethodSource("dataSetActorsUpdateDeathdate")
    void updateDeathdate(final Long id, final LocalDate deathdate, final boolean result, final Actor domain, final ActorEntity entity) {
        when(jdbcActorRepository.updateDeathdate(id, deathdate)).thenReturn(result);
        when(jdbcActorRepository.findById(id)).thenReturn(Optional.of(entity.withDeathdate(deathdate)));

        assertThat(actorRepository.updateDeathdate(id, deathdate)).hasValue(domain.withDeathdate(deathdate));
    }

    @Test
    void updateDeathdate_NonExisting() {
        when(jdbcActorRepository.updateDeathdate(Long.MAX_VALUE, LocalDate.now())).thenReturn(false);

        assertThat(actorRepository.updateDeathdate(Long.MAX_VALUE, LocalDate.now())).isEmpty();
    }

    @ParameterizedTest @MethodSource("dataSetFindAllByGender")
    void findAllByGender(final Gender gender, final List<Actor> domain, final List<ActorEntity> entity) {
        when(jdbcActorRepository.findAllByGender(gender.name())).thenReturn(entity);

        assertThat(actorRepository.findAllByGender(gender)).isEqualTo(domain);
    }


    static Stream<Arguments> dataSetCreate() {
        return Stream.of(
                Arguments.of(phoenixDomain, phoenixEntity),
                Arguments.of(deNiroDomain, deNiroEntity),
                Arguments.of(beniginiDomain, beniginiEntity),
                Arguments.of(bonacelliDomain, bonacelliEntity),
                Arguments.of(knightleyDomain, knightleyEntity)
        );
    }

    static Stream<Arguments> dataSetCreateAll() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), Collections.emptyList()),
                Arguments.of(Collections.singletonList(phoenixDomain), Collections.singletonList(phoenixEntity)),
                Arguments.of(Arrays.asList(phoenixDomain, deNiroDomain), Arrays.asList(phoenixEntity, deNiroEntity))
        );
    }


    static Stream<Arguments> dataSetFindAll() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), Collections.emptyList()),
                Arguments.of(Collections.singletonList(phoenixDomain), Collections.singletonList(phoenixEntity)),
                Arguments.of(Arrays.asList(phoenixDomain, deNiroDomain), Arrays.asList(phoenixEntity, deNiroEntity))
        );
    }

    static Stream<Arguments> dataSetFindById() {
        return Stream.of(
                Arguments.of(1L, phoenixDomain, phoenixEntity),
                Arguments.of(2L, deNiroDomain, deNiroEntity),
                Arguments.of(3L, beniginiDomain, beniginiEntity),
                Arguments.of(4L, bonacelliDomain, bonacelliEntity),
                Arguments.of(5L, knightleyDomain, knightleyEntity)
        );
    }

    static Stream<Arguments> dataSetFindByFullname() {
        return Stream.of(
                Arguments.of("Joaquin Phoenix", phoenixDomain, phoenixEntity),
                Arguments.of("Robert De Niro", deNiroDomain, deNiroEntity),
                Arguments.of("Roberto Benigini", beniginiDomain, beniginiEntity),
                Arguments.of("Paolo Bonacelli", bonacelliDomain, bonacelliEntity),
                Arguments.of("Keira Knightley", knightleyDomain, knightleyEntity)
        );
    }

    static Stream<Arguments> dataSetActorsUpdateDeathdate() {
        return Stream.of(
                Arguments.of(1L, LocalDate.of(2044, 12, 31), true, phoenixDomain, phoenixEntity),
                Arguments.of(2L, LocalDate.of(2020, 1, 1), true, deNiroDomain, deNiroEntity),
                Arguments.of(3L, LocalDate.of(2016, 12, 1), true, beniginiDomain, beniginiEntity),
                Arguments.of(4L, LocalDate.of(2100, 9, 9), true, bonacelliDomain, bonacelliEntity),
                Arguments.of(5L, LocalDate.of(2029, 1, 11), true, knightleyDomain, knightleyEntity)
        );
    }


    static Stream<Arguments> dataSetFindAllByGender() {
        return Stream.of(
                Arguments.of(
                        Gender.Male,
                        Arrays.asList(phoenixDomain, deNiroDomain, beniginiDomain, bonacelliDomain),
                        Arrays.asList(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity)
                ),
                Arguments.of(Gender.Female, Collections.singletonList(knightleyDomain), Collections.singletonList(knightleyEntity)),
                Arguments.of(Gender.Other, Collections.emptyList(), Collections.emptyList())
        );
    }

}