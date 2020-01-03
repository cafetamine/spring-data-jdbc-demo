package com.cafetamine.spring.data.jdbc.demo.core.application.genre;

import com.cafetamine.spring.data.jdbc.demo.core.domain.genre.Genre;
import com.cafetamine.spring.data.jdbc.demo.repository.movie.MovieGenreReference;

import java.util.List;
import java.util.Optional;


public interface IGenreRepository {

    Optional<Genre> findById(Integer id);

    Optional<Genre> findByName(String name);

    Genre create(Genre genre);

    List<Genre> createAll(List<Genre> genres);

    List<Genre> findAllByReference(final List<MovieGenreReference> references);

}
