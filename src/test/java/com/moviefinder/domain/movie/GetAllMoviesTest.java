package com.moviefinder.domain.movie;

import com.moviefinder.application.usecases.GetAllMoviesInteractor;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.ports.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

public class GetAllMoviesTest {


    private MovieRepository repo;
    private GetAllMoviesInteractor service;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(MovieRepository.class);
        service = new GetAllMoviesInteractor(repo);

        Mockito.when(repo.save(any(Movie.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
    }

    @Test
    void getAllMovies_should_delegate_to_repo() {
        Mockito.when(repo.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(service.execute())
                .verifyComplete();

        Mockito.verify(repo).findAll();
    }
}
