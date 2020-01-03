package com.cafetamine.spring.data.jdbc.demo.repository.movie;

import com.cafetamine.spring.data.jdbc.demo.core.domain.actor.Actor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import org.springframework.data.relational.core.mapping.Table;

import java.util.Map;
import java.util.stream.Collectors;


@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
@Table("MOVIE_ACTORS")
public class MovieActorReference {

    Long ActorId;


    static MovieActorReference fromDomain(final Actor actor) {
        return new MovieActorReference(actor.getId());
    }

    static Map<String, MovieActorReference> formDomain(final Map<String, Actor> actors) {
        return actors.entrySet()
                     .stream()
                     .collect(Collectors.toMap(
                             Map.Entry::getKey,
                             e -> fromDomain(e.getValue())
                     ));
    }

}