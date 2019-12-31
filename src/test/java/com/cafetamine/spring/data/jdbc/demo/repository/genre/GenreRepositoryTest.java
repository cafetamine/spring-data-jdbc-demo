package com.cafetamine.spring.data.jdbc.demo.repository.genre;

import com.cafetamine.spring.data.jdbc.demo.domain.genre.Genre;
import com.cafetamine.spring.data.jdbc.demo.domain.genre.IGenreRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
class GenreRepositoryTest {

    private static final JdbcGenreRepository jdbcGenreRepository = mock(JdbcGenreRepository.class);
    private static final IGenreRepository actorRepository = new GenreRepository(jdbcGenreRepository);

    private GenreEntity comedyEntity, tragedyEntity;
    private Genre comedy, tragedy;

    @BeforeEach
    void beforeEach() {
        comedyEntity = new GenreEntity(1, "comedy");
        tragedyEntity = new GenreEntity(2, "tragedy");
        comedy = new Genre(1, "comedy");
        tragedy = new Genre(2, "tragedy");
    }

    @Test
    void findByName() {
        when(jdbcGenreRepository.findByName("comedy")).thenReturn(Optional.of(comedyEntity));
        when(jdbcGenreRepository.findByName("tragedy")).thenReturn(Optional.of(tragedyEntity));
        when(jdbcGenreRepository.findByName("non existing")).thenReturn(Optional.empty());

        assertThat(actorRepository.findByName("comedy")).hasValue(comedy);
        assertThat(actorRepository.findByName("tragedy")).hasValue(tragedy);
        assertThat(actorRepository.findByName("non existing")).isEmpty();
    }

    @Test
    void create_Existing() {
        when(jdbcGenreRepository.findByName("comedy")).thenReturn(Optional.of(comedyEntity));

        assertThat(actorRepository.create(new Genre(1, "comedy"))).isEqualTo(comedy);
    }

    @Test
    void create_NonExisting() {
        final GenreEntity thrillerEntity = new GenreEntity(null, "thriller");
        final Genre thriller = new Genre(3, "thriller");

        when(jdbcGenreRepository.findByName("thriller")).thenReturn(Optional.empty());
        when(jdbcGenreRepository.save(thrillerEntity)).thenReturn(thrillerEntity);

        assertThat(actorRepository.create(thriller)).hasFieldOrPropertyWithValue("name", "thriller");
    }

}