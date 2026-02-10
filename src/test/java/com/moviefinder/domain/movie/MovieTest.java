package com.moviefinder.domain.movie;

import com.moviefinder.domain.entities.Actor;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.enums.Genre;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovieTest {

    @Test
    void should_fail_when_name_blank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Movie(null, " ", Genre.ACTION, LocalDate.of(2020, 1, 1),
                        List.of(new Actor(UUID.randomUUID(), "John", "Doe"))));
    }

    @Test
    void should_fail_when_genre_null() {
        assertThrows(IllegalArgumentException.class,
                () -> new Movie(null, "Movie", null, LocalDate.of(2020, 1, 1),
                        List.of(new Actor(UUID.randomUUID(), "John", "Doe"))));
    }

    @Test
    void should_fail_when_publicationDate_null() {
        assertThrows(IllegalArgumentException.class,
                () -> new Movie(null, "Movie", Genre.ACTION, null,
                        List.of(new Actor(UUID.randomUUID(), "John", "Doe"))));
    }

    @Test
    void should_fail_when_actors_empty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Movie(null, "Movie", Genre.ACTION, LocalDate.of(2020, 1, 1),
                        List.of()));
    }

    @Test
    void should_fail_when_publicationDate_in_future() {
        Clock fixedClock = Clock.fixed(
                Instant.parse("2020-01-01T00:00:00Z"),
                ZoneOffset.UTC
        );

        Movie movie = new Movie(
                null,
                "Movie",
                Genre.ACTION,
                LocalDate.of(2020, 1, 2),
                List.of(new Actor(UUID.randomUUID(), "John", "Doe"))
        );

        assertThrows(IllegalArgumentException.class, () -> movie.validateNotInFuture(fixedClock));
    }

    @Test
    void should_pass_when_publicationDate_not_in_future() {
        Clock fixedClock = Clock.fixed(
                Instant.parse("2020-01-01T00:00:00Z"),
                ZoneOffset.UTC
        );

        Movie movie = new Movie(
                null,
                "Movie",
                Genre.ACTION,
                LocalDate.of(2020, 1, 1),
                List.of(new Actor(UUID.randomUUID(), "John", "Doe"))
        );

        assertDoesNotThrow(() -> movie.validateNotInFuture(fixedClock));
    }
}
