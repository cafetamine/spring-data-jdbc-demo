package com.cafetamine.spring.data.jdbc.demo.repository.movie;

import com.cafetamine.spring.data.jdbc.demo.core.application.actor.IActorRepository;
import com.cafetamine.spring.data.jdbc.demo.core.application.genre.IGenreRepository;
import com.cafetamine.spring.data.jdbc.demo.core.application.movie.IMovieRepository;
import com.cafetamine.spring.data.jdbc.demo.core.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.core.domain.def.Gender;
import com.cafetamine.spring.data.jdbc.demo.core.domain.genre.Genre;
import com.cafetamine.spring.data.jdbc.demo.core.domain.movie.Movie;
import com.cafetamine.spring.data.jdbc.demo.repository.actor.ActorRepository;
import com.cafetamine.spring.data.jdbc.demo.repository.genre.GenreRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
class MovieRepositoryTest {

    private static final JdbcMovieRepository jdbcMovieRepository = mock(JdbcMovieRepository.class);
    private static final IActorRepository actorRepository = mock(ActorRepository.class);
    private static final IGenreRepository genreRepository = mock(GenreRepository.class);
    private static final IMovieRepository movieRepository = new MovieRepository(jdbcMovieRepository, actorRepository, genreRepository);

    private static final MovieGenreReference crimeRef = new MovieGenreReference(1);
    private static final MovieGenreReference dramaRef = new MovieGenreReference(2);
    private static final MovieGenreReference thrillerRef = new MovieGenreReference(3);
    private static final MovieGenreReference comedyRef = new MovieGenreReference(4);

    private static final MovieActorReference phoenixRef = new MovieActorReference(1L);
    private static final MovieActorReference deNiroRef = new MovieActorReference(2L);
    private static final MovieActorReference beniginiRef = new MovieActorReference(3L);
    private static final MovieActorReference bonacelliRef = new MovieActorReference(4L);
    private static final MovieActorReference lawrenceRef = new MovieActorReference(6L);

    private static final MovieAggregate jockerAggregate = new MovieAggregate(
            1L,
            "Jocker",
            122L,
            LocalDate.of(2019, 4, 19),
            Map.of("Arthur Fleck", phoenixRef, "Murray Franklin", deNiroRef),
            Arrays.asList(crimeRef, dramaRef, thrillerRef)
    );
    private static final MovieAggregate nightAggreagate = new MovieAggregate(
            2L,
            "Night on Earth",
            129L,
            LocalDate.of(1991, 12, 12),
            Map.of("Driver (segment \"Rome\")", beniginiRef, "Priest (segment \"Rome\")", bonacelliRef),
            Arrays.asList(comedyRef, dramaRef)
    );

    private static final Genre crimeDomain = new Genre(1, "crime");
    private static final Genre dramaDomain = new Genre(2, "drama");
    private static final Genre thrillerDomain = new Genre(3, "thriller");
    private static final Genre comedyDomain = new Genre(4, "comedy");
    private static final Genre westernDomain = new Genre(5, "western");

    private static final Actor phoenixDomain = new Actor(1L, "Joaquin", "Phoenix", LocalDate.of(1974, 10, 28), null, Gender.Male);
    private static final Actor deNiroDomain = new Actor(2L, "Robert", "De Niro", LocalDate.of(1943, 8, 17), null, Gender.Male);
    private static final Actor beniginiDomain = new Actor(3L, "Roberto", "Benigini", LocalDate.of(1952, 10, 27), null, Gender.Male);
    private static final Actor bonacelliDomain = new Actor(4L, "Paolo", "Bonacelli", LocalDate.of(1937, 2, 28), null, Gender.Male);
    private static final Actor knightleyDomain = new Actor(5L, "Keira", "Knightley", LocalDate.of(1985, 4, 26), null, Gender.Female);
    private static final Actor lawrenceDomain = new Actor(6L, "Jennifer", "Lawrence",  LocalDate.of(1990, 7, 15), null, Gender.Female);

    private static final Movie jockerDomain = new Movie(
            1L,
            "Jocker",
            Duration.ofSeconds(122L),
            LocalDate.of(2019, 4, 19),
            Map.of("Arthur Fleck", phoenixDomain, "Murray Franklin", deNiroDomain),
            Arrays.asList(crimeDomain, dramaDomain, thrillerDomain)
    );
    private static final Movie nightDomain = new Movie(
            2L,
            "Night on Earth",
            Duration.ofSeconds(129L),
            LocalDate.of(1991, 12, 12),
            Map.of("Driver (segment \"Rome\")", beniginiDomain, "Priest (segment \"Rome\")", bonacelliDomain),
            Arrays.asList(comedyDomain, dramaDomain)
    );


    @Test
    void findAll() {
        when(jdbcMovieRepository.findAll()).thenReturn(Arrays.asList(jockerAggregate, nightAggreagate));

        when(actorRepository.findAllByReference(jockerAggregate.getActors())).thenReturn(jockerDomain.getActors());
        when(genreRepository.findAllByReference(jockerAggregate.getGenres())).thenReturn(jockerDomain.getGenres());

        when(actorRepository.findAllByReference(nightAggreagate.getActors())).thenReturn(nightDomain.getActors());
        when(genreRepository.findAllByReference(nightAggreagate.getGenres())).thenReturn(nightDomain.getGenres());

        assertThat(movieRepository.findAll()).containsAll(Arrays.asList(jockerDomain, nightDomain));
    }

    @Test
    void create() {
        final Movie movieDomain = new Movie(
                3L,
                "Joy",
                Duration.ofSeconds(124L),
                LocalDate.of(2015,12, 13),
                Map.of("Joy", lawrenceDomain, "Rudy", deNiroDomain),
                Collections.singletonList(dramaDomain)
        );
        final MovieAggregate movieAggregate = new MovieAggregate(
                3L,
                "Joy",
                124L,
                LocalDate.of(2015,12, 13),
                Map.of("Joy", lawrenceRef, "Rudy", deNiroRef),
                Collections.singletonList(dramaRef)
        );
        when(jdbcMovieRepository.save(movieAggregate)).thenReturn(movieAggregate);

        when(genreRepository.createAll(Collections.singletonList(dramaDomain))).thenReturn(Collections.singletonList(dramaDomain));
        when(actorRepository.createAll(new ArrayList<>(movieDomain.getActors().values()))).thenReturn(new ArrayList<>(movieDomain.getActors().values()));

        when(actorRepository.findAllByReference(movieAggregate.getActors())).thenReturn(movieDomain.getActors());
        when(genreRepository.findAllByReference(movieAggregate.getGenres())).thenReturn(movieDomain.getGenres());

        assertThat(movieRepository.create(movieDomain)).isEqualTo(movieDomain);
    }

    @ParameterizedTest @MethodSource("dataSetFindById")
    void findById(final Long id, final MovieAggregate aggregate, final Movie domain) {
        when(jdbcMovieRepository.findById(id)).thenReturn(Optional.of(aggregate));

        when(actorRepository.findAllByReference(aggregate.getActors())).thenReturn(domain.getActors());
        when(genreRepository.findAllByReference(aggregate.getGenres())).thenReturn(domain.getGenres());

        assertThat(movieRepository.findById(id)).hasValue(domain);
    }

    @Test
    void findById_NonExisting() {
        when(jdbcMovieRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThat(movieRepository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @ParameterizedTest @MethodSource("dataSetFindAllByGenre")
    void findAllByGenre(final Genre genre, final List<MovieAggregate> aggregate, final List<Movie> domain) {
        when(jdbcMovieRepository.findAllByGenreId(genre.getId())).thenReturn(new HashSet<>(aggregate));
        IntStream.range(0, aggregate.size()).forEach(i -> {
            when(actorRepository.findAllByReference(aggregate.get(i).getActors())).thenReturn(domain.get(i).getActors());
            when(genreRepository.findAllByReference(aggregate.get(i).getGenres())).thenReturn(domain.get(i).getGenres());
        });

        assertThat(movieRepository.findAllByGenre(genre)).containsExactlyInAnyOrderElementsOf(domain);
    }

    @ParameterizedTest @MethodSource("dataSetFindAllByActor")
    void findAllByActor(final Actor actor, final List<MovieAggregate> aggregate, final List<Movie> domain) {
        when(jdbcMovieRepository.findAllByActorId(actor.getId())).thenReturn(new HashSet<>(aggregate));
        IntStream.range(0, aggregate.size()).forEach(i -> {
            when(actorRepository.findAllByReference(aggregate.get(i).getActors())).thenReturn(domain.get(i).getActors());
            when(genreRepository.findAllByReference(aggregate.get(i).getGenres())).thenReturn(domain.get(i).getGenres());
        });

        assertThat(movieRepository.findAllByActor(actor)).containsExactlyInAnyOrderElementsOf(domain);
    }


    static Stream<Arguments> dataSetFindById() {
        return Stream.of(
                Arguments.of(1L, jockerAggregate, jockerDomain),
                Arguments.of(2L, nightAggreagate, nightDomain)
        );
    }

    static  Stream<Arguments> dataSetFindAllByGenre() {
        return Stream.of(
                Arguments.of(westernDomain, Collections.emptyList(), Collections.emptyList()),
                Arguments.of(comedyDomain, Collections.singletonList(nightAggreagate), Collections.singletonList(nightDomain)),
                Arguments.of(dramaDomain, Arrays.asList(jockerAggregate, nightAggreagate), Arrays.asList(jockerDomain, nightDomain))
        );
    }

    static Stream<Arguments> dataSetFindAllByActor() {
        return Stream.of(
                Arguments.of(knightleyDomain, Collections.emptyList(), Collections.emptyList()),
                Arguments.of(phoenixDomain, Collections.singletonList(jockerAggregate), Collections.singletonList(jockerDomain))
                // TODO test for multiple shared actors
        );
    }

}
