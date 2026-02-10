package com.moviefinder.infrastructure.persistence.repositories;

import com.moviefinder.infrastructure.persistence.entities.MovieEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.UUID;

public interface MovieEntityRepository extends ReactiveCrudRepository<MovieEntity, UUID> {

    @Query("SELECT * FROM movies WHERE publication_date BETWEEN :startDate AND :endDate")
    Flux<MovieEntity> findByPublicationDateBetween(LocalDate startDate, LocalDate endDate);
}
