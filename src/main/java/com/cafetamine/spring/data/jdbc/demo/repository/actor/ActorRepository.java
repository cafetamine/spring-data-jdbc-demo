package com.cafetamine.spring.data.jdbc.demo.repository.actor;

import com.cafetamine.spring.data.jdbc.demo.domain.actor.Actor;
import com.cafetamine.spring.data.jdbc.demo.domain.actor.IActorRepository;
import com.cafetamine.spring.data.jdbc.demo.domain.def.Gender;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Component
@AllArgsConstructor
public class ActorRepository implements IActorRepository {

    private final JdbcActorRepository actorRepository;


    @Override
    public List<Actor> findAll() {
        return StreamSupport.stream(actorRepository.findAll().spliterator(), false)
                .map(ActorEntity::toDomain).collect(Collectors.toList());
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
        if (actorRepository.updateDeathdate(id, deathdate)) {
            return findById(id);
        }
        return Optional.empty();
    }

    @Override
    public List<Actor> findAllByGender(final Gender gender) {
        return actorRepository.findAllByGender(gender.name())
                              .stream()
                              .map(ActorEntity::toDomain)
                              .collect(Collectors.toList());
    }

}
