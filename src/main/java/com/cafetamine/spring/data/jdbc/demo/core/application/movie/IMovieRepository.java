package com.cafetamine.spring.data.jdbc.demo.core.application.movie;

import com.cafetamine.spring.data.jdbc.demo.core.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.core.domain.genre.Genre;
import com.cafetamine.spring.data.jdbc.demo.core.domain.movie.Movie;

import java.util.List;
import java.util.Optional;


public interface IMovieRepository {

    List<Movie> findAll();

    Movie create(Movie movie);

    Optional<Movie> findById(Long id);

    List<Movie> findAllByGenre(Genre genre);

    List<Movie> findAllByActor(Actor actor);

}
