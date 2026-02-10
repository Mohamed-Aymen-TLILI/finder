package com.moviefinder.domain.movie;

import com.moviefinder.application.usecases.CreateMovieInteractor;
import com.moviefinder.domain.entities.Actor;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.enums.Genre;
import com.moviefinder.domain.ports.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CreateMovieTest {

    private MovieRepository repo;
    private Clock clock;
    private CreateMovieInteractor create;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(MovieRepository.class);
        clock = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC);
        create = new CreateMovieInteractor(repo, clock);

        when(repo.save(any(Movie.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
    }

    @Test
    void create_should_generate_movie_id_and_actor_ids_when_missing() {
        Movie input = new Movie(
                null,
                "Movie",
                Genre.ACTION,
                LocalDate.of(2019, 12, 31),
                List.of(
                        new Actor(null, "John", "Doe"),
                        new Actor(null, "Jane", "Smith")
                )
        );

        when(repo.save(any(Movie.class))).thenAnswer(inv -> {
            Movie m = inv.getArgument(0);

            return Mono.just(new Movie(
                    UUID.randomUUID(),
                    m.name(),
                    m.genre(),
                    m.publicationDate(),
                    List.of(
                            new Actor(UUID.randomUUID(), m.actors().get(0).firstname(), m.actors().get(0).lastname()),
                            new Actor(UUID.randomUUID(), m.actors().get(1).firstname(), m.actors().get(1).lastname())
                    )
            ));
        });

        StepVerifier.create(create.execute(input))
                .assertNext(saved -> {
                    assertNotNull(saved.id());
                    assertEquals(2, saved.actors().size());
                    assertNotNull(saved.actors().get(0).id());
                    assertNotNull(saved.actors().get(1).id());
                })
                .verifyComplete();
    }


    @Test
    void create_should_fail_when_publicationDate_in_future() {
        Movie input = new Movie(
                null,
                "Movie",
                Genre.ACTION,
                LocalDate.of(2020, 1, 2),
                List.of(new Actor(null, "John", "Doe"))
        );

        StepVerifier.create(create.execute(input))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}