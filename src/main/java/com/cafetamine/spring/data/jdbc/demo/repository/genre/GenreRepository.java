package com.cafetamine.spring.data.jdbc.demo.repository.genre;

import com.cafetamine.spring.data.jdbc.demo.domain.exception.DataIntegrityException;
import com.cafetamine.spring.data.jdbc.demo.domain.genre.Genre;
import com.cafetamine.spring.data.jdbc.demo.domain.genre.IGenreRepository;
import com.cafetamine.spring.data.jdbc.demo.repository.movie.MovieGenresReference;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@AllArgsConstructor
public class GenreRepository implements IGenreRepository {

    private final JdbcGenreRepository genreRepository;


    @Override
    public Optional<Genre> findById(final Integer id) {
        return genreRepository.findById(id).map(GenreEntity::toDomain);
    }

    @Override
    public Optional<Genre> findByName(final String name) {
        return genreRepository.findByName(name).map(GenreEntity::toDomain);
    }

    @Override
    public Genre create(final Genre genre) {
        return findByName(genre.getName()).orElseGet(() -> genreRepository.save(GenreEntity.fromDomain(genre)).toDomain());
    }

    @Override
    public List<Genre> create(final List<Genre> genres) {
        return genres.stream()
                     .map(this::create)
                     .collect(Collectors.toList());
    }

    @Override
    public List<Genre> findAllByReference(final List<MovieGenresReference> references) {
        return references.stream()
                         .map(reference -> findById(reference.getGenreId())
                         .orElseThrow(DataIntegrityException::new))
                         .collect(Collectors.toList());
    }

}
