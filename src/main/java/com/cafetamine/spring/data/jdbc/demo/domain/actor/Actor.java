package com.cafetamine.spring.data.jdbc.demo.domain.actor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
public class Actor {

    Long id;
    String name, surname;
    LocalDate birthdate, deathdate;

    public String getFullName() {
        return String.format("%s %s", name, surname);
    }

}
