package com.cafetamine.spring.data.jdbc.demo.repository.actor;

import com.cafetamine.spring.data.jdbc.demo.SpringDataJdbcDemoApplicationTestsConfiguration;
import com.cafetamine.spring.data.jdbc.demo.core.domain.def.Gender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;


@DataJdbcTest
@Import(SpringDataJdbcDemoApplicationTestsConfiguration.class)
class JdbcActorRepositoryTest {

    private static final ActorEntity phoenixEntity = new ActorEntity(1L, "Joaquin", "Phoenix", LocalDate.of(1974, 10, 28), null, Gender.Male);
    private static final ActorEntity deNiroEntity = new ActorEntity(2L, "Robert", "De Niro", LocalDate.of(1943, 8, 17), null, Gender.Male);
    private static final ActorEntity beniginiEntity = new ActorEntity(3L, "Roberto", "Benigini", LocalDate.of(1952, 10, 27), null, Gender.Male);
    private static final ActorEntity bonacelliEntity = new ActorEntity(4L, "Paolo", "Bonacelli", LocalDate.of(1937, 2, 28), null, Gender.Male);
    private static final ActorEntity knightleyEntity = new ActorEntity(5L, "Keira", "Knightley", LocalDate.of(1985, 4, 26), null, Gender.Female);
    private static final ActorEntity lawrenceEntity = new ActorEntity(6L, "Jennifer", "Lawrence", LocalDate.of(1990, 7, 15), null, Gender.Female);


    @Autowired private JdbcActorRepository repository;


    @Test
    void save() {
        final ActorEntity saved = repository.save(new ActorEntity(null, "Johnny", "Depp", LocalDate.of(1963, 6, 9), null, Gender.Male));
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).hasValue(saved);
    }

    @Test
    void saveAll() {
        final Iterable<ActorEntity> saved = repository.saveAll(Arrays.asList(
                new ActorEntity(null, "Johnny", "Depp", LocalDate.of(1963, 6, 9), null, Gender.Male),
                new ActorEntity(null, "Orlando", "Bloom",  LocalDate.of(1977, 1, 13), null, Gender.Male)
        ));
        assertThat(repository.findAll()).containsAll(saved);
    }

    @ParameterizedTest @MethodSource("dataSetFindById")
    void findById(final Long id, final ActorEntity expected) {
        assertThat(repository.findById(id)).hasValue(expected);
    }

    @Test
    void findById_NonExisting() {
        assertThat(repository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @ParameterizedTest @MethodSource("dataSetExistsById")
    void existsById(final Long id, final boolean expected) {
        assertThat(repository.existsById(id)).isEqualTo(expected);
    }

    @Test
    void findAll() {
        assertThat(repository.findAll()).containsExactlyInAnyOrder(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity, knightleyEntity, lawrenceEntity);
    }

    @ParameterizedTest @MethodSource("dataSetFindAllById")
    void findAllById(final Iterable<Long> ids, final Iterable<ActorEntity> expected) {
        assertThat(repository.findAllById(ids)).containsExactlyElementsOf(expected);
    }

    @Test
    void count() {
        assertThat(repository.count()).isEqualTo(6L);
    }

    @Test
    void deleteById() {
        repository.deleteById(5L); // "Keira Knightley" - does not have any db relation constraints
        assertThat(repository.count()).isEqualTo(5L);
        assertThat(repository.findById(5L)).isEmpty();
        assertThat(repository.findAll()).doesNotContain(knightleyEntity);
    }

    @Test
    void deleteById_ThrowsForDbRelationConstraint() {
        assertThat(catchThrowableOfType(() ->
                repository.deleteById(1L), // "Joaquin Phoenix" - has a db relation constraint
                DbActionExecutionException.class
        )).isNotNull();
        // isTransactional
        assertThat(repository.count()).isEqualTo(6L);
        assertThat(repository.findAll()).containsExactlyInAnyOrder(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity, knightleyEntity, lawrenceEntity);
    }

    @Test
    void delete() {
        repository.delete(knightleyEntity); // "Keira Knightley" - does not have any db relation constraints
        assertThat(repository.count()).isEqualTo(5L);
        assertThat(repository.findById(knightleyEntity.getId())).isEmpty();
        assertThat(repository.findAll()).doesNotContain(knightleyEntity);
    }

    @Test
    void delete_ThrowsForDbRelationConstraint() {
        assertThat(catchThrowableOfType(() ->
                repository.delete(phoenixEntity), // "Joaquin Phoenix" - has a db relation constraint
                DbActionExecutionException.class
        )).isNotNull();
        // isTransactional
        assertThat(repository.count()).isEqualTo(6L);
        assertThat(repository.findAll()).containsExactlyInAnyOrder(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity, knightleyEntity, lawrenceEntity);
    }

    @ParameterizedTest @MethodSource("dataSetDeleteAllFromIterable")
    void deleteAllFromIterable(final Iterable<ActorEntity> actors, final Iterable<ActorEntity> expected) {
        repository.deleteAll(actors);
        assertThat(repository.findAll()).containsExactlyElementsOf(expected);
    }

    @Test
    void deleteAll_ThrowsForDbRelationConstraint() {
        assertThat(catchThrowableOfType(() ->
                repository.deleteAll(),
                DbActionExecutionException.class
        )).isNotNull();
        // isTransactional
        assertThat(repository.count()).isEqualTo(6L);
        assertThat(repository.findAll()).containsExactlyInAnyOrder(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity, knightleyEntity, lawrenceEntity);
    }

    @ParameterizedTest @MethodSource("dataSetFindByFullname")
    void findByFullname(final String fullname, final ActorEntity expected) {
        assertThat(repository.findByFullname(fullname)).hasValue(expected);
    }

    @Test
    void findByFullname_NonExisting() {
        assertThat(repository.findByFullname("Non Existing")).isEmpty();
    }

    @ParameterizedTest @MethodSource("dataSetFindAllByGender")
    void findAllByGender(final Gender gender, final Iterable<ActorEntity> expected) {
        assertThat(repository.findAllByGender(gender.name())).containsExactlyElementsOf(expected);
    }

    @ParameterizedTest @MethodSource("dataSetUpdateDeathdate")
    void updateDeathdate(final Long id, final LocalDate deathdate, final boolean successFlag) {
        final boolean isSuccess = repository.updateDeathdate(id, deathdate); // sooorryy

        assertThat(isSuccess).isEqualTo(successFlag);
        assertThat(repository.findById(id)).hasValueSatisfying(actual ->
                assertThat(actual.getDeathdate()).isEqualTo(deathdate)
        );
    }

    @Test
    void updateDeathdate_NonExisting() {
        assertThat(repository.updateDeathdate(Long.MAX_VALUE, LocalDate.now())).isFalse();
    }


    static Stream<Arguments> dataSetFindById() {
        return Stream.of(
                Arguments.of(1L, phoenixEntity),
                Arguments.of(2L, deNiroEntity),
                Arguments.of(3L, beniginiEntity),
                Arguments.of(4L, bonacelliEntity),
                Arguments.of(5L, knightleyEntity)
        );
    }

    static Stream<Arguments> dataSetExistsById() {
        return Stream.of(
                Arguments.of(1L, true),
                Arguments.of(2L, true),
                Arguments.of(3L, true),
                Arguments.of(4L, true),
                Arguments.of(5L, true) ,
                Arguments.of(Long.MAX_VALUE, false)
        );
    }

    static Stream<Arguments> dataSetFindAllById() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), Collections.emptyList()),
                Arguments.of(Collections.singletonList(1), Collections.singletonList(phoenixEntity)),
                Arguments.of(Collections.singletonList(Long.MAX_VALUE), Collections.emptyList()),
                Arguments.of(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L), Arrays.asList(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity, knightleyEntity, lawrenceEntity)),
                Arguments.of(Arrays.asList(Long.MAX_VALUE, 1L, 2L, 3L, 4L, 5L, 6L), Arrays.asList(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity, knightleyEntity, lawrenceEntity))
        );
    }

    static Stream<Arguments> dataSetDeleteAllFromIterable() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), Arrays.asList(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity, knightleyEntity, lawrenceEntity)),
                Arguments.of(Collections.singletonList(knightleyEntity), Arrays.asList(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity, lawrenceEntity)),
                Arguments.of(Arrays.asList(knightleyEntity, lawrenceEntity), Arrays.asList(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity)),
                Arguments.of(
                        Arrays.asList(knightleyEntity, lawrenceEntity, new ActorEntity(Long.MAX_VALUE, "NonExisting", "NonExisting", LocalDate.now(), LocalDate.now(), Gender.Other)),
                        Arrays.asList(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity)
                )
        );
    }

    static Stream<Arguments> dataSetFindByFullname() {
        return Stream.of(
                Arguments.of("Joaquin Phoenix", phoenixEntity),
                Arguments.of("Robert De Niro", deNiroEntity),
                Arguments.of("Roberto Benigini", beniginiEntity),
                Arguments.of("Paolo Bonacelli", bonacelliEntity),
                Arguments.of("Keira Knightley", knightleyEntity),
                Arguments.of("Jennifer Lawrence", lawrenceEntity)
        );
    }

    static Stream<Arguments> dataSetFindAllByGender() {
        return Stream.of(
                Arguments.of(Gender.Male, Arrays.asList(phoenixEntity, deNiroEntity, beniginiEntity, bonacelliEntity)),
                Arguments.of(Gender.Female, Arrays.asList(knightleyEntity, lawrenceEntity)),
                Arguments.of(Gender.Other, Collections.emptyList())
        );
    }

    static Stream<Arguments> dataSetUpdateDeathdate() {
        return Stream.of(
                Arguments.of(1L, LocalDate.of(1900, 12, 1), true),
                Arguments.of(2L, LocalDate.of(1950, 1, 31), true),
                Arguments.of(3L, LocalDate.of(1975, 12, 31), true),
                Arguments.of(4L, LocalDate.of(2020, 1, 1), true),
                Arguments.of(5L, LocalDate.of(2100, 5, 5), true),
                Arguments.of(6L, LocalDate.of(1993, 11, 11), true)
        );
    }

}
