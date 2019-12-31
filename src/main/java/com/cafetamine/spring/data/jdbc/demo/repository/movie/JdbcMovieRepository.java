package com.cafetamine.spring.data.jdbc.demo.repository.movie;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface JdbcMovieRepository extends CrudRepository<MovieAggregate, Long> {

    @Query("SELECT * FROM MOVIES JOIN MOVIE_GENRES ON MOVIES.Id = MOVIE_GENRES.MovieId")
    Set<MovieAggregate> findByGenreId(final Integer genreId);

    @Query("SELECT * FROM MOVIES JOIN MOVIE_ACTORS ON MOVIES.Id = MOVIE_ACTORS.MovieId")
    Set<MovieAggregate> findByActorId(final Long actorId);

}
