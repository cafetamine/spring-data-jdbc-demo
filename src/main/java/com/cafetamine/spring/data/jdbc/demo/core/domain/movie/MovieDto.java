package com.cafetamine.spring.data.jdbc.demo.core.domain.movie;

import com.cafetamine.spring.data.jdbc.demo.core.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.core.domain.genre.Genre;

import lombok.Value;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Value
public class MovieDto {

    Long id;
    String title;
    Duration duration;
    LocalDate releaseDate;
    Map<String, Actor> actors;
    List<Genre> genres;

}
