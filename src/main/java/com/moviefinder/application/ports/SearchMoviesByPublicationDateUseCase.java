package com.moviefinder.application.ports;

import com.moviefinder.domain.entities.Movie;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface SearchMoviesByPublicationDateUseCase {
    Flux<Movie> execute(LocalDate startDate, LocalDate endDate);
}
