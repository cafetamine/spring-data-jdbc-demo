package com.cafetamine.spring.data.jdbc.demo.repository.genre;

import com.cafetamine.spring.data.jdbc.demo.domain.genre.Genre;
import com.cafetamine.spring.data.jdbc.demo.domain.genre.IGenreRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@AllArgsConstructor
public class GenreRepository implements IGenreRepository {

    private final JdbcGenreRepository genreRepository;


    @Override
    public Optional<Genre> findByName(final String name) {
        return genreRepository.findByName(name).map(GenreEntity::toDomain);
    }

    @Override
    public Genre create(final Genre genre) {
        return findByName(genre.getName()).orElseGet(() -> genreRepository.save(GenreEntity.fromDomain(genre)).toDomain());
    }

}
