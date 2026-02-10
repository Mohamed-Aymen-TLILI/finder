package com.moviefinder.application.usecases;

import com.moviefinder.application.ports.CreateMovieUseCase;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.ports.MovieRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Clock;

@Component
public class CreateMovieInteractor implements CreateMovieUseCase {

    private final MovieRepository repo;
    private final Clock clock;

    public CreateMovieInteractor(MovieRepository repo, Clock clock) {
        this.repo = repo;
        this.clock = clock;
    }

    @Override
    public Mono<Movie> execute(Movie movie) {
        return Mono.justOrEmpty(movie)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Movie is required")))
                .handle((m, sink) -> {
                    if (m.id() != null) sink.error(new IllegalArgumentException("Movie id must be null on creation"));
                    else if (m.actors().stream().anyMatch(a -> a.id() != null)) sink.error(new IllegalArgumentException("Actor ids must be null on creation"));
                    else sink.next(m);
                })
                .cast(Movie.class)
                .doOnNext(m -> m.validateNotInFuture(clock))
                .flatMap(repo::save);
    }

}
