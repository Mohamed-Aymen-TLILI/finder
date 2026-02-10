package com.moviefinder.domain.entities;

import com.moviefinder.domain.enums.Genre;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Movie(
        UUID id,
        String name,
        Genre genre,
        LocalDate publicationDate,
        List<Actor> actors
) {
    public Movie {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Movie name is required");
        }
        if (genre == null) {
            throw new IllegalArgumentException("Movie genre is required");
        }
        if (publicationDate == null) {
            throw new IllegalArgumentException("Movie publicationDate is required");
        }
        actors = (actors == null) ? List.of() : List.copyOf(actors);
        if (actors.isEmpty()) {
            throw new IllegalArgumentException("Movie must have at least one actor");
        }
    }

    public void validateNotInFuture(Clock clock) {
        if (publicationDate.isAfter(LocalDate.now(clock))) {
            throw new IllegalArgumentException("Movie publicationDate cannot be in the future");
        }
    }

    public Movie withId(UUID newId) {
        return new Movie(newId, name, genre, publicationDate, actors);
    }

    public Movie withActors(List<Actor> newActors) {
        return new Movie(id, name, genre, publicationDate, newActors);
    }
}
