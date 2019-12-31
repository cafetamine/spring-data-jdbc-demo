package com.cafetamine.spring.data.jdbc.demo.repository.movie;

import com.cafetamine.spring.data.jdbc.demo.core.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.core.domain.genre.Genre;
import com.cafetamine.spring.data.jdbc.demo.core.domain.movie.Movie;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
@Table("MOVIES")
class MovieAggregate {

    @Id Long id;
    String title;
    @Column("Duration") Long durationSeconds;
    LocalDate releaseDate;

    @MappedCollection(idColumn = "MovieId", keyColumn = "Role")
    Map<String, MovieActorsReference> actors;

    @MappedCollection(idColumn = "MovieId", keyColumn = "Significance")
    List<MovieGenresReference> genres;


    Movie toDomain(final Map<String, Actor> actors, final List<Genre> genres) {
        return new Movie(id, title, Duration.ofSeconds(durationSeconds), releaseDate, actors, genres);
    }


    static MovieAggregate fromDomain(final Movie movie) {
        return new MovieAggregate(
                movie.getId(),
                movie.getTitle(),
                movie.getDuration().toSeconds(),
                movie.getReleaseDate(),
                MovieActorsReference.formDomain(movie.getActors()),
                MovieGenresReference.fromDomain(movie.getGenres())
        );
    }

}
