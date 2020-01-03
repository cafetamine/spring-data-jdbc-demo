package com.cafetamine.spring.data.jdbc.demo.repository.actor;

import com.cafetamine.spring.data.jdbc.demo.core.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.core.application.actor.IActorRepository;
import com.cafetamine.spring.data.jdbc.demo.core.domain.def.Gender;
import com.cafetamine.spring.data.jdbc.demo.core.domain.exception.DataIntegrityException;
import com.cafetamine.spring.data.jdbc.demo.repository.movie.MovieActorReference;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Component
@AllArgsConstructor
public class ActorRepository implements IActorRepository {

    private final JdbcActorRepository actorRepository;


    @Override
    public Actor create(final Actor actor) {
         return actorRepository.save(ActorEntity.fromDomain(actor)).toDomain();
    }

    @Override
    public List<Actor> createAll(final List<Actor> actors) {
        return StreamSupport.stream(actorRepository.saveAll(
                    actors.stream()
                          .map(ActorEntity::fromDomain)
                          .collect(Collectors.toList())
                ).spliterator(), false)
                .map(ActorEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Actor> findAll() {
        return StreamSupport.stream(actorRepository.findAll().spliterator(), false)
                            .map(ActorEntity::toDomain)
                            .collect(Collectors.toList());
    }

    @Override
    public Optional<Actor> findById(final Long id) {
        return actorRepository.findById(id).map(ActorEntity::toDomain);
    }

    @Override
    public Optional<Actor> findByFullname(final String fullname) {
        return actorRepository.findByFullname(fullname).map(ActorEntity::toDomain);
    }

    @Override
    public Optional<Actor> updateDeathdate(final Long id, final LocalDate deathdate) {
        return actorRepository.updateDeathdate(id, deathdate) ? findById(id) : Optional.empty();
    }

    @Override
    public List<Actor> findAllByGender(final Gender gender) {
        return actorRepository.findAllByGender(gender.name())
                              .stream()
                              .map(ActorEntity::toDomain)
                              .collect(Collectors.toList());
    }

    @Override
    public Map<String, Actor> findAllByReference(final Map<String, MovieActorReference> references) {
        return references.entrySet()
                         .stream()
                         .collect(Collectors.toMap(
                                 Map.Entry::getKey,
                                 reference -> findById(reference.getValue().getActorId())
                         .orElseThrow(DataIntegrityException::new)));
    }

}
