package com.moviefinder.infrastructure.persistence.repositories;

import com.moviefinder.infrastructure.persistence.entities.ActorEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ActorEntityRepository extends ReactiveCrudRepository<ActorEntity, UUID> {

    @Query("""
    SELECT a.* FROM actors a
    JOIN movie_actors ma ON a.id = ma.actor_id
    WHERE ma.movie_id = :movieId
    """)
    Flux<ActorEntity> findByMovieId(UUID movieId);
}
