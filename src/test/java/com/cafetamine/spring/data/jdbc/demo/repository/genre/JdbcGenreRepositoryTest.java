package com.cafetamine.spring.data.jdbc.demo.repository.genre;

import com.cafetamine.spring.data.jdbc.demo.SpringDataJdbcDemoApplicationTestsConfiguration;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;


@DataJdbcTest
@Import(SpringDataJdbcDemoApplicationTestsConfiguration.class)
class JdbcGenreRepositoryTest {

    @Autowired JdbcGenreRepository repository;

    private static final GenreEntity crimeEntity = new GenreEntity(1, "crime");
    private static final GenreEntity dramaEntity = new GenreEntity(2, "drama");
    private static final GenreEntity thrillerEntity = new GenreEntity(3, "thriller");
    private static final GenreEntity comedyEntity = new GenreEntity(4, "comedy");
    private static final GenreEntity westernEntity = new GenreEntity(5, "western");
    private static final GenreEntity sifiEntity = new GenreEntity(6, "si-fi");


    @Test
    void save() {
        final GenreEntity saved = repository.save(new GenreEntity(null, "animation"));
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).hasValue(saved);
    }

    @Test
    void saveAll() {
        final Iterable<GenreEntity> saved = repository.saveAll(Arrays.asList(
                new GenreEntity(null, "animation"),
                new GenreEntity(null, "documentary")
        ));
        assertThat(repository.findAll()).containsAll(saved);
    }

    @ParameterizedTest @MethodSource("dataSetFindById")
    void findById(final Integer id, final GenreEntity expected) {
        assertThat(repository.findById(id)).hasValue(expected);
    }

    @Test
    void findById_NonExisting() {
        assertThat(repository.findById(Integer.MAX_VALUE)).isEmpty();
    }

    @ParameterizedTest @MethodSource("dataSetExistsById")
    void existsById(final Integer id, final boolean expected) {
        assertThat(repository.existsById(id)).isEqualTo(expected);
    }

    @Test
    void findAll() {
        assertThat(repository.findAll()).containsExactlyInAnyOrder(crimeEntity, dramaEntity, thrillerEntity, comedyEntity, westernEntity, sifiEntity);
    }

    @ParameterizedTest @MethodSource("dataSetFindAllById")
    void findAllById(final Iterable<Integer> ids, final Iterable<GenreEntity> expected) {
        assertThat(repository.findAllById(ids)).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void count() {
        assertThat(repository.count()).isEqualTo(6L);
    }

    @Test
    void deleteById() {
        repository.deleteById(5); // "western" - does not have any db relation constraints
        assertThat(repository.count()).isEqualTo(5);
        assertThat(repository.findById(5)).isEmpty();
        assertThat(repository.findAll()).doesNotContain(westernEntity);
    }

    @Test
    void deleteById_ThrowsForDbRelationConstraint() {
        assertThat(catchThrowableOfType(() ->
                repository.deleteById(1), // "crime" - has a db relation constraint
                DbActionExecutionException.class
        )).isNotNull();
        // is transactional
        assertThat(repository.count()).isEqualTo(6L);
        assertThat(repository.findAll()).containsExactlyInAnyOrder(crimeEntity, dramaEntity, thrillerEntity, comedyEntity, westernEntity, sifiEntity);
    }

    @Test
    void delete() {
        repository.delete(westernEntity); // "western" - does not have any db relation constraints
        assertThat(repository.count()).isEqualTo(5L);
        assertThat(repository.findById(westernEntity.getId())).isEmpty();
        assertThat(repository.findAll()).doesNotContain(westernEntity);
    }

    @Test
    void delete_ThrowsForDbRelationConstraint() {
        assertThat(catchThrowableOfType(() ->
                repository.delete(crimeEntity), // "crime" - has a db relation constraint
                DbActionExecutionException.class
        )).isNotNull();
        // is transactional
        assertThat(repository.count()).isEqualTo(6L);
        assertThat(repository.findAll()).containsExactlyInAnyOrder(crimeEntity, dramaEntity, thrillerEntity, comedyEntity, westernEntity, sifiEntity);
    }

    @ParameterizedTest @MethodSource("dataSetDeleteAllFromIterable")
    void deleteAllFromIterable(final Iterable<GenreEntity> genres, final Iterable<GenreEntity> expected) {
        repository.deleteAll(genres);
        assertThat(repository.findAll()).containsExactlyElementsOf(expected);
    }

    @Test
    void deleteAll_ThrowsForDbRelationConstraint() {
        assertThat(catchThrowableOfType(() ->
                repository.deleteAll(),
                DbActionExecutionException.class
        )).isNotNull();
        // is transactional
        assertThat(repository.count()).isEqualTo(6L);
        assertThat(repository.findAll()).containsExactlyInAnyOrder(crimeEntity, dramaEntity, thrillerEntity, comedyEntity, westernEntity, sifiEntity);
    }

    @ParameterizedTest @MethodSource("dataSetFindByName")
    void findByName(final String name, final GenreEntity expected) {
        assertThat(repository.findByName(name)).hasValue(expected);
    }

    @Test
    void findByName_NonExisting() {
        assertThat(repository.findByName("NonExisting")).isEmpty();
    }


    static Stream<Arguments> dataSetFindById() {
        return Stream.of(
                Arguments.of(1, crimeEntity),
                Arguments.of(2, dramaEntity),
                Arguments.of(3, thrillerEntity),
                Arguments.of(4, comedyEntity),
                Arguments.of(5, westernEntity),
                Arguments.of(6, sifiEntity)
        );
    }

    static Stream<Arguments> dataSetExistsById() {
        return Stream.of(
                Arguments.of(1, true),
                Arguments.of(2, true),
                Arguments.of(3, true),
                Arguments.of(4, true),
                Arguments.of(5, true),
                Arguments.of(6, true),
                Arguments.of(Integer.MAX_VALUE, false)
        );
    }

    static Stream<Arguments>  dataSetFindAllById() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), Collections.emptyList()),
                Arguments.of(Collections.singletonList(1), Collections.singletonList(crimeEntity)),
                Arguments.of(Collections.singletonList(Integer.MAX_VALUE), Collections.emptyList()),
                Arguments.of(Arrays.asList(1, 2, 3, 4, 5, 6), Arrays.asList(crimeEntity, dramaEntity, thrillerEntity, comedyEntity, westernEntity, sifiEntity)),
                Arguments.of(Arrays.asList(Integer.MAX_VALUE, 1, 2, 3, 4, 5, 6), Arrays.asList(crimeEntity, dramaEntity, thrillerEntity, comedyEntity, westernEntity, sifiEntity))
        );
    }

    static Stream<Arguments> dataSetDeleteAllFromIterable() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), Arrays.asList(crimeEntity, dramaEntity, thrillerEntity, comedyEntity, westernEntity, sifiEntity)),
                Arguments.of(Collections.singletonList(westernEntity), Arrays.asList(crimeEntity, dramaEntity, thrillerEntity, comedyEntity, sifiEntity)),
                Arguments.of(Arrays.asList(westernEntity, sifiEntity), Arrays.asList(crimeEntity, dramaEntity, thrillerEntity, comedyEntity)),
                Arguments.of(Arrays.asList(westernEntity, sifiEntity, new GenreEntity(Integer.MAX_VALUE, "non-existing")), Arrays.asList(crimeEntity, dramaEntity, thrillerEntity, comedyEntity))
        );
    }

    static Stream<Arguments> dataSetFindByName() {
        return Stream.of(
                Arguments.of("crime", crimeEntity),
                Arguments.of("drama", dramaEntity),
                Arguments.of("thriller", thrillerEntity),
                Arguments.of("comedy", comedyEntity),
                Arguments.of("western", westernEntity),
                Arguments.of("si-fi", sifiEntity)
        );
    }

}
