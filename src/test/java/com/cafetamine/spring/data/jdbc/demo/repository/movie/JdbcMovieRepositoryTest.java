package com.cafetamine.spring.data.jdbc.demo.repository.movie;

import com.cafetamine.spring.data.jdbc.demo.SpringDataJdbcDemoApplicationTestsConfiguration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@DataJdbcTest
@Import(SpringDataJdbcDemoApplicationTestsConfiguration.class)
class JdbcMovieRepositoryTest {

    private static final MovieGenreReference crimeRef = new MovieGenreReference(1);
    private static final MovieGenreReference dramaRef = new MovieGenreReference(2);
    private static final MovieGenreReference thrillerRef = new MovieGenreReference(3);
    private static final MovieGenreReference comedyRef = new MovieGenreReference(4);

    private static final MovieActorReference phoenixRef = new MovieActorReference(1L);
    private static final MovieActorReference deNiroRef = new MovieActorReference(2L);
    private static final MovieActorReference beniginiRef = new MovieActorReference(3L);
    private static final MovieActorReference bonacelliRef = new MovieActorReference(4L);
    private static final MovieActorReference lawrenceRef = new MovieActorReference(6L);


    @Autowired JdbcMovieRepository repository;


    private static final MovieAggregate jocker = new MovieAggregate(
            1L,
            "Jocker",
            122L,
            LocalDate.of(2019, 4, 19),
            new HashMap<>() {{
                put("Arthur Fleck", phoenixRef);
                put("Murray Franklin", deNiroRef);
            }},
            Arrays.asList(crimeRef, dramaRef, thrillerRef)
    );

    private static final MovieAggregate night = new MovieAggregate(
            2L,
            "Night on Earth",
            129L,
            LocalDate.of(1991, 12, 12),
            new HashMap<>() {{
                put("Driver (segment \"Rome\")", beniginiRef);
                put("Priest (segment \"Rome\")", bonacelliRef);
            }},
            Arrays.asList(comedyRef, dramaRef)
    );


    @Test
    void save() {
        final MovieAggregate saved = repository.save(new MovieAggregate(
                null,
                "Joy",
                124L,
                LocalDate.of(2015,12, 13),
                new HashMap<>() {{
                    put("Joy", lawrenceRef);
                    put("Rudy", deNiroRef);
                }},
                Collections.singletonList(dramaRef)
        ));
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).hasValue(saved);
    }

    @Test
    void saveAll() {
        final Iterable<MovieAggregate> saved = repository.saveAll(Arrays.asList(
                new MovieAggregate(
                        null,
                        "Joy",
                        124L,
                        LocalDate.of(2015,12, 13),
                        new HashMap<>() {{
                            put("Joy", lawrenceRef);
                            put("Rudy", deNiroRef);
                        }},
                        Collections.singletonList(dramaRef)
                ),
                new MovieAggregate(
                        null,
                        "La vita è bella",
                        116L,
                        LocalDate.of(1997,12, 20),
                        new HashMap<>() {{
                            put("Guido", beniginiRef);
                        }},
                        Arrays.asList(comedyRef, dramaRef)
                )
        ));
        assertThat(repository.findAll()).containsAll(saved);
    }

    @ParameterizedTest @MethodSource("dataSetFindById")
    void findById(final Long id, final MovieAggregate expected) {
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
        assertThat(repository.findAll()).containsExactlyInAnyOrder(jocker, night);
    }

    @ParameterizedTest @MethodSource("dataSetFindAllById")
    void findAllById(final Iterable<Long> ids, final Iterable<MovieAggregate> expected) {
        assertThat(repository.findAllById(ids)).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void count() {
        assertThat(repository.count()).isEqualTo(2L);
    }

    @Test
    void deleteById() {
        repository.deleteById(1L);
        assertThat(repository.count()).isEqualTo(1L);
        assertThat(repository.findById(1L)).isEmpty();
        assertThat(repository.findAll()).doesNotContain(jocker);
    }

    @Test
    void delete() {
        repository.delete(jocker);
        assertThat(repository.count()).isEqualTo(1L);
        assertThat(repository.findById(jocker.getId())).isEmpty();
        assertThat(repository.findAll()).doesNotContain(jocker);
    }

    @ParameterizedTest @MethodSource("dataSetDeleteAllFromIterable")
    void deleteAllFromIterable(final Iterable<MovieAggregate> movies, final Iterable<MovieAggregate> expected) {
        repository.deleteAll(movies);
        assertThat(repository.findAll()).containsExactlyElementsOf(expected);
    }

    @ParameterizedTest @MethodSource("dataSetFindByGenreId")
    void findByGenreId(final Integer genreId, final Iterable<MovieAggregate> expected) {
        assertThat(repository.findAllByGenreId(genreId)).containsAll(expected);
    }

    @ParameterizedTest @MethodSource("dataSetFindByActorId")
    void findByActorId(final Long actorId, final Iterable<MovieAggregate> expected) {
        assertThat(repository.findAllByActorId(actorId)).containsAll(expected);
    }


    static Stream<Arguments> dataSetFindById() {
        return Stream.of(
                Arguments.of(1L, jocker),
                Arguments.of(2L, night)
        );
    }

    static Stream<Arguments> dataSetExistsById() {
        return Stream.of(
                Arguments.of(1L, true),
                Arguments.of(2L, true),
                Arguments.of(Long.MAX_VALUE, false)
        );
    }

    static Stream<Arguments>  dataSetFindAllById() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), Collections.emptyList()),
                Arguments.of(Collections.singletonList(Integer.MAX_VALUE), Collections.emptyList()),
                Arguments.of(Collections.singletonList(1L), Collections.singletonList(jocker)),
                Arguments.of(Arrays.asList(1L, 2L), Arrays.asList(jocker, night)),
                Arguments.of(Arrays.asList(Integer.MAX_VALUE, 1L, 2L), Arrays.asList(jocker, night))
        );
    }

    static Stream<Arguments> dataSetDeleteAllFromIterable() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), Arrays.asList(jocker, night)),
                Arguments.of(Collections.singletonList(jocker), Collections.singletonList(night)),
                Arguments.of(Arrays.asList(jocker, night), Collections.emptyList()),
                Arguments.of(
                        Arrays.asList(jocker, new MovieAggregate(Long.MAX_VALUE, "NonExisting", 111L, LocalDate.now(), Collections.emptyMap(), Collections.emptyList())),
                        Collections.singletonList(night)
                )
        );
    }

    static Stream<Arguments> dataSetFindByGenreId() {
        return Stream.of(
                Arguments.of(Integer.MAX_VALUE, Collections.emptySet()),
                Arguments.of(5, Collections.emptySet()),
                Arguments.of(1, Collections.singleton(jocker)),
                Arguments.of(2, Set.of(jocker, night))
        );
    }

    static  Stream<Arguments> dataSetFindByActorId() {
        return Stream.of(
                Arguments.of(Long.MAX_VALUE, Collections.emptySet()),
                Arguments.of(1L, Collections.singleton(jocker)),
                Arguments.of(3L, Collections.singleton(night))
                // TODO test for multiple shared actors
        );
    }

}
