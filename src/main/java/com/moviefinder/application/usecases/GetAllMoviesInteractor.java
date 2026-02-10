package com.moviefinder.application.usecases;

import com.moviefinder.application.ports.GetAllMoviesUseCase;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.ports.MovieRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class GetAllMoviesInteractor implements GetAllMoviesUseCase {

    private final MovieRepository repo;

    public GetAllMoviesInteractor(MovieRepository repo) {
        this.repo = repo;
    }

    @Override
    public Flux<Movie> execute() {
        return repo.findAll();
    }
}
