package com.moviefinder.application.usecases;

import com.moviefinder.application.errors.MovieNotFoundException;
import com.moviefinder.application.ports.GetMovieByIdUseCase;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.ports.MovieRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class GetMovieByIdInteractor implements GetMovieByIdUseCase {

    private final MovieRepository repo;

    public GetMovieByIdInteractor(MovieRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Movie> execute(UUID id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new MovieNotFoundException(id)));
    }
}
