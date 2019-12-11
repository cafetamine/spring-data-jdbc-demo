package com.cafetamine.spring.data.jdbc.demo.domain.actor;

import com.cafetamine.spring.data.jdbc.demo.domain.def.Gender;
import lombok.Value;

import java.time.LocalDate;


@Value
public class ActorDto {

    Long id;
    String name, surname;
    LocalDate birthdate, deathdate;
    Gender gender;

}
