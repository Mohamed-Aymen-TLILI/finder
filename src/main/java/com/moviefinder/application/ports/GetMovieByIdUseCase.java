package com.moviefinder.application.ports;

import com.moviefinder.domain.entities.Movie;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GetMovieByIdUseCase {
    Mono<Movie> execute(UUID id);
}
