package com.moviefinder.domain.movie;

import com.moviefinder.application.errors.MovieNotFoundException;
import com.moviefinder.application.usecases.GetMovieByIdInteractor;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.ports.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

public class GetMovieTest {

    private MovieRepository repo;
    private GetMovieByIdInteractor service;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(MovieRepository.class);
        service = new GetMovieByIdInteractor(repo);

        Mockito.when(repo.save(any(Movie.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
    }

    @Test
    void getMovieById_should_throw_when_not_found() {
        UUID id = UUID.randomUUID();
        Mockito.when(repo.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(service.execute(id))
                .expectError(MovieNotFoundException.class)
                .verify();
    }

}
