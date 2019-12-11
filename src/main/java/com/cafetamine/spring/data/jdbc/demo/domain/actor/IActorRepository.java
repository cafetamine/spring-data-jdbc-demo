package com.cafetamine.spring.data.jdbc.demo.domain.actor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface IActorRepository {

    List<Actor> findAll();

    Optional<Actor> findById(Long id);

    Optional<Actor> findByFullname(String fullname);

    Optional<Actor> updateDeathdate(Long id, LocalDate deathdate);

}
