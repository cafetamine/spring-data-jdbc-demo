package com.cafetamine.spring.data.jdbc.demo.repository.actor;

import com.cafetamine.spring.data.jdbc.demo.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.domain.def.Gender;

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
public class ActorEntity {

    @Id @With Long id;
    String name, surname;
    LocalDate birthdate, deathdate;
    Gender gender;


    public Actor toDomain() {
        return new Actor(id, name, surname, birthdate, deathdate, gender);
    }

    static ActorEntity fromDomain(final Actor actor) {
        return new ActorEntity(
                actor.getId(),
                actor.getName(),
                actor.getSurname(),
                actor.getBirthdate(),
                actor.getDeathdate(),
                actor.getGender()
        );
    }

}
