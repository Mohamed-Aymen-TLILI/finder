package com.moviefinder.application.ports;
import com.moviefinder.domain.entities.Movie;
import reactor.core.publisher.Mono;

public interface CreateMovieUseCase {
    Mono<Movie> execute(Movie movie);
}
