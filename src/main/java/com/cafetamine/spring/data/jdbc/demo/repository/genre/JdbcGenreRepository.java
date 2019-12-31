package com.cafetamine.spring.data.jdbc.demo.repository.genre;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface JdbcGenreRepository extends CrudRepository<GenreEntity, Integer> {

    @Query("SELECT * FROM GENRES WHERE Name = :name")
    Optional<GenreEntity> findByName(String name);

}
