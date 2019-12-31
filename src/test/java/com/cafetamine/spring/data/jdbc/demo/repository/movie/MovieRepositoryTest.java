package com.cafetamine.spring.data.jdbc.demo.repository.movie;

import com.cafetamine.spring.data.jdbc.demo.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.domain.actor.IActorRepository;
import com.cafetamine.spring.data.jdbc.demo.domain.def.Gender;
import com.cafetamine.spring.data.jdbc.demo.domain.genre.Genre;
import com.cafetamine.spring.data.jdbc.demo.domain.genre.IGenreRepository;
import com.cafetamine.spring.data.jdbc.demo.domain.movie.IMovieRepository;
import com.cafetamine.spring.data.jdbc.demo.domain.movie.Movie;
import com.cafetamine.spring.data.jdbc.demo.repository.actor.ActorRepository;
import com.cafetamine.spring.data.jdbc.demo.repository.genre.GenreRepository;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
class MovieRepositoryTest {

    private static final JdbcMovieRepository jdbcMovieRepository = mock(JdbcMovieRepository.class);
    private static final IActorRepository actorRepository = mock(ActorRepository.class);
    private static final IGenreRepository genreRepository = mock(GenreRepository.class);
    private static final IMovieRepository movieRepository = new MovieRepository(jdbcMovieRepository, actorRepository, genreRepository);


    private Map<String, Actor> jockerActors, nightActors;
    private List<Genre> jockerGenres, nightGenres;
    private Movie jocker, night;

    @BeforeEach
    void beforeEach() {
        jockerActors = new HashMap<>() {{
            put("Arthur Fleck", new Actor(1L, "Joaquin", "Phoenix", LocalDate.of(1974, 10, 28), null, Gender.Male));
            put("Murray Franklin", new Actor(2L, "Robert", "De Niro", LocalDate.of(1943, 8, 17), null, Gender.Male));
        }};
        jockerGenres = Arrays.asList(
                new Genre(1, "crime"),
                new Genre(2, "drama"),
                new Genre(3, "thriller")
        );
        jocker = new Movie(1L, "Jocker", Duration.ofSeconds(122L), LocalDate.of(2019, 10, 4), jockerActors, jockerGenres);

        nightActors = new HashMap<>() {{
            put("Driver (segment \"Rome\")", new Actor(3L, "Roberto", "Benigini", LocalDate.of(1952, 10, 27), null, Gender.Male));
            put("Priest (segment \"Rome\")", new Actor(4L, "Paolo", "Bonacelli", LocalDate.of(1937, 2, 28), null, Gender.Male));
        }};
        nightGenres = Arrays.asList(
                new Genre(4, "comedy"),
                new Genre(2, "drama")
        );
        night = new Movie(2L, "Night on Earth", Duration.ofSeconds(129L), LocalDate.of(1991, 12, 12), nightActors, nightGenres);
    }


    @Test
    void findAll() {
        when(jdbcMovieRepository.findAll()).thenReturn(Arrays.asList(
                MovieAggregate.fromDomain(jocker),
                MovieAggregate.fromDomain(night)
        ));
        when(actorRepository.findAllByReference(MovieAggregate.fromDomain(jocker).getActors())).thenReturn(jockerActors);
        when(actorRepository.findAllByReference(MovieAggregate.fromDomain(night).getActors())).thenReturn(nightActors);
        when(genreRepository.findAllByReference(MovieAggregate.fromDomain(jocker).getGenres())).thenReturn(jockerGenres);
        when(genreRepository.findAllByReference(MovieAggregate.fromDomain(night).getGenres())).thenReturn(nightGenres);

        assertThat(movieRepository.findAll()).isEqualTo(Arrays.asList(jocker, night));
    }

    @Test
    void create() {
        final List<Genre> spiritedGenres = Arrays.asList(new Genre(5, "animation"), new Genre(6, "adventure"));
        final HashMap<String, Actor> spiritedActors = new HashMap<>() {{
            put("Chihiro Ogino (voice)", new Actor(5L, "Rumi", "Hiiragi", LocalDate.of(1987, 8, 1), null, Gender.Female));
            put("Haku (voice)", new Actor(6L, "Miyu", "Irino", LocalDate.of(1988, 2, 19), null, Gender.Male));
        }};
        final Movie spirited = new Movie(3L, "Sen to Chihiro no kamikakushi", Duration.ofSeconds(125L), LocalDate.of(2001, 7, 20), spiritedActors, spiritedGenres);

        when(genreRepository.create(spiritedGenres)).thenReturn(spiritedGenres);
        when(actorRepository.create(new ArrayList<>(spiritedActors.values()))).thenReturn(new ArrayList<>(spiritedActors.values()));
        when(jdbcMovieRepository.save(MovieAggregate.fromDomain(spirited))).thenReturn(MovieAggregate.fromDomain(spirited));
        when(actorRepository.findAllByReference(MovieAggregate.fromDomain(spirited).getActors())).thenReturn(spiritedActors);
        when(genreRepository.findAllByReference(MovieAggregate.fromDomain(spirited).getGenres())).thenReturn(spiritedGenres);

        assertThat(movieRepository.create(spirited)).isEqualTo(spirited);
    }

    @Test
    void findById() {
        when(jdbcMovieRepository.findById(1L)).thenReturn(Optional.of(MovieAggregate.fromDomain(jocker)));
        when(actorRepository.findAllByReference(MovieAggregate.fromDomain(jocker).getActors())).thenReturn(jockerActors);
        when(genreRepository.findAllByReference(MovieAggregate.fromDomain(jocker).getGenres())).thenReturn(jockerGenres);

        assertThat(movieRepository.findById(1L)).hasValue(jocker);
    }

    @Test
    void findByGenre() {
        when(jdbcMovieRepository.findByGenreId(2)).thenReturn(Sets.newLinkedHashSet(
                MovieAggregate.fromDomain(jocker),
                MovieAggregate.fromDomain(night)
        ));
        when(actorRepository.findAllByReference(MovieAggregate.fromDomain(jocker).getActors())).thenReturn(jockerActors);
        when(actorRepository.findAllByReference(MovieAggregate.fromDomain(night).getActors())).thenReturn(nightActors);
        when(genreRepository.findAllByReference(MovieAggregate.fromDomain(jocker).getGenres())).thenReturn(jockerGenres);
        when(genreRepository.findAllByReference(MovieAggregate.fromDomain(night).getGenres())).thenReturn(nightGenres);

        assertThat(movieRepository.findByGenre(new Genre(2, "drama"))).isEqualTo(Arrays.asList(jocker, night));
    }

    @Test
    void findByActor() {
        when(jdbcMovieRepository.findByActorId(1L)).thenReturn(Sets.newLinkedHashSet(MovieAggregate.fromDomain(jocker)));
        when(actorRepository.findAllByReference(MovieAggregate.fromDomain(jocker).getActors())).thenReturn(jockerActors);
        when(genreRepository.findAllByReference(MovieAggregate.fromDomain(jocker).getGenres())).thenReturn(jockerGenres);

        assertThat(movieRepository.findByActor(
                new Actor(1L, "Joaquin", "Phoenix", LocalDate.of(1974, 10, 28), null, Gender.Male)
        )).isEqualTo(Collections.singletonList(jocker));
    }

}
