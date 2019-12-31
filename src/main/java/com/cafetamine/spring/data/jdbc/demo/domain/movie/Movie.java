package com.cafetamine.spring.data.jdbc.demo.domain.movie;

import com.cafetamine.spring.data.jdbc.demo.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.domain.genre.Genre;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
public class Movie {

    Long id;
    String title;
    Duration duration;
    LocalDate releaseDate;
    Map<String, Actor> actors;
    List<Genre> genres;

}
