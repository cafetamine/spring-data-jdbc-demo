package com.cafetamine.spring.data.jdbc.demo.repository.movie;

import com.cafetamine.spring.data.jdbc.demo.core.domain.genre.Genre;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.stream.Collectors;


@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
@Table("MOVIE_GENRES")
public class MovieGenreReference {

    Integer genreId;


    static MovieGenreReference fromDomain(final Genre genre) {
        return new MovieGenreReference(genre.getId());
    }

    static List<MovieGenreReference> fromDomain(final List<Genre> genres) {
        return genres.stream().map(MovieGenreReference::fromDomain).collect(Collectors.toList());
    }

}
