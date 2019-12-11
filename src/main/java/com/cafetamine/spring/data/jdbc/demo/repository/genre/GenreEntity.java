package com.cafetamine.spring.data.jdbc.demo.repository.genre;

import com.cafetamine.spring.data.jdbc.demo.domain.genre.Genre;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import lombok.experimental.FieldDefaults;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
@Table("GENRE")
class GenreEntity {

    @Id @With Integer id;
    String name;


    Genre toDomain() {
        return new Genre(name);
    }

    static GenreEntity fromDomain(final Genre genre) {
        return new GenreEntity(null, genre.getName());
    }

}