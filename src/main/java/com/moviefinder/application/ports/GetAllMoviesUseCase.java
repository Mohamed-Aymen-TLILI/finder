package com.moviefinder.application.ports;

import com.moviefinder.domain.entities.Movie;
import reactor.core.publisher.Flux;

public interface GetAllMoviesUseCase {
    Flux<Movie> execute();
}
