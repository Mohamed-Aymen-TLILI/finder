package com.moviefinder.infrastructure.persistence.mapper;

import com.moviefinder.domain.entities.Actor;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.infrastructure.persistence.entities.ActorEntity;
import com.moviefinder.infrastructure.persistence.entities.MovieEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersistenceMapper {

    public Actor toDomain(ActorEntity e) {
        return new Actor(e.id(), e.firstname(), e.lastname());
    }

    public Movie toDomain(MovieEntity e, List<Actor> actors) {
        return new Movie(e.id(), e.name(), e.genre(), e.publicationDate(), actors);
    }

}
