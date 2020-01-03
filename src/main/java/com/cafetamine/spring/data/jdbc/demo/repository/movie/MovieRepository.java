package com.cafetamine.spring.data.jdbc.demo.repository.movie;

import com.cafetamine.spring.data.jdbc.demo.core.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.core.application.actor.IActorRepository;
import com.cafetamine.spring.data.jdbc.demo.core.domain.genre.Genre;
import com.cafetamine.spring.data.jdbc.demo.core.application.genre.IGenreRepository;
import com.cafetamine.spring.data.jdbc.demo.core.application.movie.IMovieRepository;
import com.cafetamine.spring.data.jdbc.demo.core.domain.movie.Movie;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Component
@AllArgsConstructor
public class MovieRepository implements IMovieRepository {

    private final JdbcMovieRepository movieRepository;
    private final IActorRepository actorRepository;
    private final IGenreRepository genreRepository;


    @Override
    public List<Movie> findAll() {
        return StreamSupport.stream(movieRepository.findAll().spliterator(), false)
                            .map(this::aggregateMovie)
                            .collect(Collectors.toList());
    }

    @Override
    public Movie create(final Movie movie) {
        return aggregateMovie(movieRepository.save(MovieAggregate.fromDomain(prepareMovieAggregate(movie))));
    }

    @Override
    public Optional<Movie> findById(final Long id) {
        return movieRepository.findById(id).map(this::aggregateMovie);
    }

    @Override
    public List<Movie> findAllByGenre(final Genre genre) {
        return movieRepository.findAllByGenreId(genre.getId())
                              .stream()
                              .map(this::aggregateMovie)
                              .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findAllByActor(final Actor actor) {
        return movieRepository.findAllByActorId(actor.getId())
                              .stream()
                              .map(this::aggregateMovie)
                              .collect(Collectors.toList());
    }

    private Movie aggregateMovie(final MovieAggregate aggregate) {
        return aggregate.toDomain(
                actorRepository.findAllByReference(aggregate.getActors()),
                genreRepository.findAllByReference(aggregate.getGenres())
        );
    }

    private Movie prepareMovieAggregate(final Movie movie) {
        movie.setGenres(genreRepository.createAll(movie.getGenres()));
        movie.setActors(prepareMovieActors(movie.getActors()));
        return movie;
    }

    private Map<String, Actor> prepareMovieActors(final Map<String, Actor> actors) {
        final List<Actor> savedActors = actorRepository.createAll(new ArrayList<>(actors.values()));
        return actors.entrySet()
                     .stream()
                     .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> savedActors.stream()
                                            .filter(actor -> actor.equalIgnoringId(e.getValue()))
                                            .findFirst()
                                            .orElse(e.getValue()))
                     );
    }

}
