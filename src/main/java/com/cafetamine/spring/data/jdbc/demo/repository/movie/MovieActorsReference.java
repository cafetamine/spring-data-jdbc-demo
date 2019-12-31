package com.cafetamine.spring.data.jdbc.demo.repository.movie;

import com.cafetamine.spring.data.jdbc.demo.domain.actor.Actor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import org.springframework.data.relational.core.mapping.Table;

import java.util.HashMap;
import java.util.Map;


@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
@Table("MOVIE_ACTORS")
public class MovieActorsReference {

    Long ActorId;

    static MovieActorsReference fromDomain(final Actor actor) {
        return new MovieActorsReference(actor.getId());
    }

    static Map<String, MovieActorsReference> formDomain(final Map<String, Actor> actors) {
        final Map<String, MovieActorsReference> actorsRef = new HashMap<>();
        actors.forEach((key, value) -> actorsRef.put(key, MovieActorsReference.fromDomain(value)));

        return actorsRef;
    }

}