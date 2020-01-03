package com.cafetamine.spring.data.jdbc.demo.repository.genre;

import com.cafetamine.spring.data.jdbc.demo.core.domain.exception.DataIntegrityException;
import com.cafetamine.spring.data.jdbc.demo.core.domain.genre.Genre;
import com.cafetamine.spring.data.jdbc.demo.core.application.genre.IGenreRepository;

import com.cafetamine.spring.data.jdbc.demo.repository.movie.MovieGenreReference;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;
import static org.mockito.Mockito.*;


@SpringBootTest
class GenreRepositoryTest {

    private static final JdbcGenreRepository jdbcRepository = mock(JdbcGenreRepository.class);
    private static final IGenreRepository repository = new GenreRepository(jdbcRepository);


    private static final GenreEntity crimeEntity = new GenreEntity(1, "crime");
    private static final GenreEntity dramaEntity = new GenreEntity(2, "drama");
    private static final GenreEntity thrillerEntity = new GenreEntity(3, "thriller");
    private static final GenreEntity comedyEntity = new GenreEntity(4, "comedy");
    private static final GenreEntity westernEntity = new GenreEntity(5, "western");
    private static final GenreEntity sifiEntity = new GenreEntity(6, "si-fi");

    private static final Genre crimeDomain = new Genre(1, "crime");
    private static final Genre dramaDomain = new Genre(2, "drama");
    private static final Genre thrillerDomain = new Genre(3, "thriller");
    private static final Genre comedyDomain = new Genre(4, "comedy");
    private static final Genre westernDomain = new Genre(5, "western");
    private static final Genre sifiDomain = new Genre(6, "si-fi");


    @ParameterizedTest @MethodSource("dataSetFindById")
    void findById(final Integer id, final GenreEntity entity, final Genre domain) {
        when(jdbcRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThat(repository.findById(id)).hasValue(domain);
    }

    @Test
    void findById_NonExisting() {
        when(jdbcRepository.findById(Integer.MAX_VALUE)).thenReturn(Optional.empty());

        assertThat(repository.findById(Integer.MAX_VALUE)).isEmpty();
    }

    @ParameterizedTest @MethodSource("dataSetFindByName")
    void findByName(final String name, final GenreEntity entity, final Genre domain) {
        when(jdbcRepository.findByName(name)).thenReturn(Optional.of(entity));

        assertThat(repository.findByName(name)).hasValue(domain);
    }

    @Test
    void findByName_NonExisting() {
        when(jdbcRepository.findByName("Non Existing")).thenReturn(Optional.empty());

        assertThat(repository.findByName("Non Existing")).isEmpty();
    }

    @Test
    void create() {
        when(jdbcRepository.findByName("animation")).thenReturn(Optional.of(new GenreEntity(10, "animation")));

        assertThat(repository.create(new Genre(null, "animation"))).isEqualTo(new Genre(10, "animation"));
    }

    @Test
    void create_NonExisting() {
        when(jdbcRepository.findByName("animation")).thenReturn(Optional.empty());
        when(jdbcRepository.save(new GenreEntity(null, "animation"))).thenReturn(new GenreEntity(10, "animation"));

        assertThat(repository.create(new Genre(null, "animation"))).isEqualTo(new Genre(10, "animation"));
    }

    @ParameterizedTest @MethodSource("dataSetCreateAll")
    void createAll(final Iterable<GenreEntity> entity, final List<Genre> domain) {
        entity.forEach(genre -> when(jdbcRepository.findByName(genre.getName())).thenReturn(Optional.of(genre)));

        assertThat(repository.createAll(domain)).containsAll(domain);
    }

    @ParameterizedTest @MethodSource("dataSetFindAllByReference")
    void findAllByReference(
            final List<MovieGenreReference> references,
            final Iterable<GenreEntity> entity,
            final Iterable<Genre> domain
    ) {
        entity.forEach(genre -> when(jdbcRepository.findById(genre.getId())).thenReturn(Optional.of(genre)));

        assertThat(repository.findAllByReference(references)).containsAll(domain);
    }

    @Test
    void findAllByReference_ThrowsDataIntegrityException() {
        when(jdbcRepository.findById(Integer.MAX_VALUE)).thenReturn(Optional.empty());

        assertThat(catchThrowableOfType(() ->
                repository.findAllByReference(Collections.singletonList(new MovieGenreReference(Integer.MAX_VALUE))),
                DataIntegrityException.class
        )).isNotNull();
    }


    static Stream<Arguments> dataSetFindById() {
        return Stream.of(
                Arguments.of(1, crimeEntity,    crimeDomain),
                Arguments.of(2, dramaEntity,    dramaDomain),
                Arguments.of(3, thrillerEntity, thrillerDomain),
                Arguments.of(4, comedyEntity,   comedyDomain),
                Arguments.of(5, westernEntity,  westernDomain),
                Arguments.of(6, sifiEntity,     sifiDomain)
        );
    }

    static Stream<Arguments> dataSetFindByName() {
        return Stream.of(
                Arguments.of("crime",    crimeEntity,    crimeDomain),
                Arguments.of("drama",    dramaEntity,    dramaDomain),
                Arguments.of("thriller", thrillerEntity, thrillerDomain),
                Arguments.of("comedy",   comedyEntity,   comedyDomain),
                Arguments.of("western",  westernEntity,  westernDomain),
                Arguments.of("si-fi",    sifiEntity,     sifiDomain)
        );
    }

    static Stream<Arguments> dataSetCreateAll() {
        return Stream.of(
                Arguments.of(Collections.emptyList(), Collections.emptyList()),
                Arguments.of(Collections.singletonList(crimeEntity), Collections.singletonList(crimeDomain)),
                Arguments.of(Arrays.asList(crimeEntity, dramaEntity), Arrays.asList(crimeDomain, dramaDomain))
        );
    }

    static Stream<Arguments> dataSetFindAllByReference() {
        return Stream.of(
                Arguments.of(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        Collections.singletonList(new MovieGenreReference(1)),
                        Collections.singletonList(crimeEntity),
                        Collections.singletonList(crimeDomain)
                ),
                Arguments.of(
                        Arrays.asList(new MovieGenreReference(1), new MovieGenreReference(2)),
                        Arrays.asList(crimeEntity, dramaEntity),
                        Arrays.asList(crimeDomain, dramaDomain)
                )
        );
    }

}
