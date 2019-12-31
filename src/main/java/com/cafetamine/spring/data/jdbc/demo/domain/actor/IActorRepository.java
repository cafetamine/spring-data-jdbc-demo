package com.cafetamine.spring.data.jdbc.demo.domain.actor;

import com.cafetamine.spring.data.jdbc.demo.domain.def.Gender;
import com.cafetamine.spring.data.jdbc.demo.repository.movie.MovieActorsReference;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface IActorRepository {

    Actor create(Actor actor);

    List<Actor> create(List<Actor> actors);

    List<Actor> findAll();

    Optional<Actor> findById(Long id);

    Optional<Actor> findByFullname(String fullname);

    Optional<Actor> updateDeathdate(Long id, LocalDate deathdate);

    List<Actor> findAllByGender(Gender gender);

    Map<String, Actor> findAllByReference(Map<String, MovieActorsReference> references);

}
