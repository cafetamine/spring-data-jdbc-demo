package com.cafetamine.spring.data.jdbc.demo.domain.movie;

import com.cafetamine.spring.data.jdbc.demo.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.domain.genre.Genre;

import java.util.List;
import java.util.Optional;


public interface IMovieRepository {

    List<Movie> findAll();

    Movie create(Movie movie);

    Optional<Movie> findById(Long id);

    List<Movie> findByGenre(Genre genre);

    List<Movie> findByActor(Actor actor);

}
