package com.cafetamine.spring.data.jdbc.demo.domain.genre;

import java.util.Optional;


public interface IGenreRepository {

    Optional<Genre> findByName(String name);

    Genre create(Genre genre);

}
