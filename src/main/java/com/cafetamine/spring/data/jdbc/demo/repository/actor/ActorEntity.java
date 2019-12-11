package com.cafetamine.spring.data.jdbc.demo.repository.actor;

import com.cafetamine.spring.data.jdbc.demo.domain.actor.Actor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import lombok.experimental.FieldDefaults;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;


@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
@Table("ACTORS")
class ActorEntity {

    @Id Long id;
    String name, surname;
    LocalDate birthdate, deathdate;


    Actor toDomain() {
        return new Actor(id, name, surname, birthdate, deathdate);
    }

    static ActorEntity fromDomain(final Actor actor) {
        return new ActorEntity(
                actor.getId(),
                actor.getName(),
                actor.getSurname(),
                actor.getBirthdate(),
                actor.getDeathdate()
        );
    }

}
