package com.moviefinder.domain.movie;

import com.moviefinder.application.usecases.SearchMoviesByPublicationDateInteractor;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.ports.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;

public class SearchMovieTest {

    private MovieRepository repo;
    private Clock clock;
    private SearchMoviesByPublicationDateInteractor service;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(MovieRepository.class);
        service = new SearchMoviesByPublicationDateInteractor(repo);

        Mockito.when(repo.save(any(Movie.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
    }

    @Test
    void searchByPublicationDate_should_error_when_start_after_end() {
        StepVerifier.create(service.execute(
                        LocalDate.of(2021, 2, 2),
                        LocalDate.of(2021, 2, 1)))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

}
