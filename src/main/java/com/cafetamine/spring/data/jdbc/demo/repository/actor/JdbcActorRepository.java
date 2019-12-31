package com.cafetamine.spring.data.jdbc.demo.repository.actor;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface JdbcActorRepository extends CrudRepository<ActorEntity, Long> {

    @Query("SELECT * FROM ACTORS WHERE CONCAT(Name, ' ', Surname) LIKE :fullname")
    Optional<ActorEntity> findByFullname(String fullname);

    @Modifying
    @Query("UPDATE ACTORS SET Deathdate = :deathdate WHERE Id = :id")
    boolean updateDeathdate(Long id, LocalDate deathdate);

    @Query("SELECT * FROM ACTORS WHERE Gender = :gender")
    List<ActorEntity> findAllByGender(String gender);

}
