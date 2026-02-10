package com.moviefinder.infrastructure.web.mapper;

import com.moviefinder.domain.entities.Actor;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.infrastructure.web.dto.ActorRequest;
import com.moviefinder.infrastructure.web.dto.ActorResponse;
import com.moviefinder.infrastructure.web.dto.MovieRequest;
import com.moviefinder.infrastructure.web.dto.MovieResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieDtoMapper {

    public MovieResponse toResponse(Movie movie) {
        List<ActorResponse> actors = movie.actors().stream()
                .map(this::toResponse)
                .toList();

        return new MovieResponse(
                movie.id(),
                movie.name(),
                movie.genre(),
                movie.publicationDate(),
                actors
        );
    }

    public Movie toDomain(MovieRequest req) {
        List<Actor> actors = req.actors().stream()
                .map(this::toDomain)
                .toList();

        return new Movie(
                null,
                req.name(),
                req.genre(),
                req.publicationDate(),
                actors
        );
    }

    private ActorResponse toResponse(Actor actor) {
        return new ActorResponse(actor.id(), actor.firstname(), actor.lastname());
    }

    private Actor toDomain(ActorRequest req) {
        return new Actor(null, req.firstname(), req.lastname());
    }
}
