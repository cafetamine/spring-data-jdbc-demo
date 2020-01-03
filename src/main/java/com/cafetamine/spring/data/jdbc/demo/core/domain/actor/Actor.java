package com.cafetamine.spring.data.jdbc.demo.core.domain.actor;

import com.cafetamine.spring.data.jdbc.demo.core.domain.def.Gender;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Objects;


@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
public class Actor {

    @With Long id;
    String name, surname;
    LocalDate birthdate;
    @With LocalDate deathdate;
    Gender gender;

    public String getFullName() {
        return String.format("%s %s", name, surname);
    }

    public boolean equalIgnoringId(final Actor other) {
        return this.name.equals(other.name) &&
               this.surname.equals(other.surname) &&
               this.birthdate.equals(other.birthdate) &&
               Objects.equals(this.deathdate, other.deathdate) &&
               this.gender.equals(other.gender);
    }

}
