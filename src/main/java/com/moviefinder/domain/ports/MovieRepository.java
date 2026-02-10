package com.moviefinder.domain.ports;

import com.moviefinder.domain.entities.Movie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface MovieRepository {
    Flux<Movie> findAll();
    Mono<Movie> findById(UUID id);
    Mono<Movie> save(Movie movie);
    Flux<Movie> findByPublicationDateBetween(LocalDate startDate, LocalDate endDate);
}
