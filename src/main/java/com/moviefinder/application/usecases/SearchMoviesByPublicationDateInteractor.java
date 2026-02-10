package com.moviefinder.application.usecases;

import com.moviefinder.application.ports.SearchMoviesByPublicationDateUseCase;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.ports.MovieRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Component
public class SearchMoviesByPublicationDateInteractor implements SearchMoviesByPublicationDateUseCase {

    private final MovieRepository repo;

    public SearchMoviesByPublicationDateInteractor(MovieRepository repo) {
        this.repo = repo;
    }

    @Override
    public Flux<Movie> execute(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return Flux.error(new IllegalArgumentException("startDate and endDate are required"));
        }
        if (startDate.isAfter(endDate)) {
            return Flux.error(new IllegalArgumentException("Start date must be before end date"));
        }
        return repo.findByPublicationDateBetween(startDate, endDate);
    }
}
