package com.moviefinder.infrastructure.persistence.adapter;

import com.moviefinder.domain.entities.Actor;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.ports.MovieRepository;
import com.moviefinder.infrastructure.persistence.entities.MovieEntity;
import com.moviefinder.infrastructure.persistence.mapper.PersistenceMapper;
import com.moviefinder.infrastructure.persistence.repositories.ActorEntityRepository;
import com.moviefinder.infrastructure.persistence.repositories.MovieEntityRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class MovieRepositoryAdapter implements MovieRepository {

    private final MovieEntityRepository movieRepo;
    private final ActorEntityRepository actorRepo;
    private final DatabaseClient databaseClient;
    private final PersistenceMapper mapper;
    private final TransactionalOperator transactionalOperator;

    public MovieRepositoryAdapter(
            MovieEntityRepository movieRepo,
            ActorEntityRepository actorRepo,
            DatabaseClient databaseClient,
            PersistenceMapper mapper,
            TransactionalOperator transactionalOperator
    ) {
        this.movieRepo = movieRepo;
        this.actorRepo = actorRepo;
        this.databaseClient = databaseClient;
        this.mapper = mapper;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Flux<Movie> findAll() {
        return movieRepo.findAll().flatMap(this::enrich);
    }

    @Override
    public Mono<Movie> findById(UUID id) {
        return movieRepo.findById(id).flatMap(this::enrich);
    }

    @Override
    public Flux<Movie> findByPublicationDateBetween(LocalDate startDate, LocalDate endDate) {
        return movieRepo.findByPublicationDateBetween(startDate, endDate)
                .flatMap(this::enrich);
    }

    @Override
    public Mono<Movie> save(Movie movie) {
        if (movie.actors() == null || movie.actors().isEmpty()) {
            return Mono.error(new IllegalArgumentException("movie must have at least 1 actor"));
        }

        Mono<UUID> movieIdMono = (movie.id() != null)
                ? updateMovie(movie).thenReturn(movie.id())
                : upsertMovieReturnId(movie);

        return movieIdMono
                .flatMap(movieId ->
                        upsertActors(movie.actors())
                                .collectList()
                                .flatMap(savedActors ->
                                        replaceLinks(movieId, savedActors)
                                                .thenReturn(new Movie(
                                                        movieId,
                                                        movie.name(),
                                                        movie.genre(),
                                                        movie.publicationDate(),
                                                        savedActors
                                                ))
                                )
                )
                .as(transactionalOperator::transactional);
    }


    private Mono<UUID> upsertMovieReturnId(Movie movie) {
        UUID generatedId = UUID.randomUUID();

        return databaseClient.sql("""
                INSERT INTO movies (id, name, genre, publication_date)
                VALUES (:id, :name, :genre, :publication_date)
                ON CONFLICT (name, publication_date)
                DO UPDATE SET genre = EXCLUDED.genre
                RETURNING id
                """)
                .bind("id", generatedId)
                .bind("name", movie.name())
                .bind("genre", movie.genre().name())
                .bind("publication_date", movie.publicationDate())
                .map((row, meta) -> row.get("id", UUID.class))
                .one()
                .switchIfEmpty(Mono.error(new IllegalStateException("Upsert movie failed (no id returned)")));
    }

    private Mono<Void> updateMovie(Movie movie) {
        return databaseClient.sql("""
                UPDATE movies
                SET name = :name,
                    genre = :genre,
                    publication_date = :publication_date
                WHERE id = :id
                """)
                .bind("id", movie.id())
                .bind("name", movie.name())
                .bind("genre", movie.genre().name())
                .bind("publication_date", movie.publicationDate())
                .fetch()
                .rowsUpdated()
                .flatMap(rows -> rows == 1
                        ? Mono.empty()
                        : Mono.error(new IllegalArgumentException("Movie not found for update: " + movie.id())))
                .then();
    }

    private Flux<Actor> upsertActors(Iterable<Actor> actors) {
        return Flux.fromIterable(actors)
                .concatMap(actor -> databaseClient.sql("""
                                INSERT INTO actors (id, firstname, lastname)
                                VALUES (:id, :firstname, :lastname)
                                ON CONFLICT (firstname, lastname)
                                DO UPDATE SET firstname = EXCLUDED.firstname
                                RETURNING id
                                """)
                        .bind("id", actor.id() != null ? actor.id() : UUID.randomUUID())
                        .bind("firstname", actor.firstname())
                        .bind("lastname", actor.lastname())
                        .map((row, meta) -> row.get("id", UUID.class))
                        .one()
                        .switchIfEmpty(Mono.error(new IllegalStateException("Upsert actor failed (no id returned)")))
                        .map(id -> new Actor(id, actor.firstname(), actor.lastname()))
                );
    }

    private Mono<Void> replaceLinks(UUID movieId, List<Actor> savedActors) {
        Mono<Void> deleteLinks = databaseClient.sql("""
                DELETE FROM movie_actors WHERE movie_id = :movie_id
                """)
                .bind("movie_id", movieId)
                .fetch()
                .rowsUpdated()
                .then();

        Mono<Void> insertLinks = Flux.fromIterable(savedActors)
                .concatMap(actor -> databaseClient.sql("""
                                INSERT INTO movie_actors (movie_id, actor_id)
                                VALUES (:movie_id, :actor_id)
                                ON CONFLICT (movie_id, actor_id) DO NOTHING
                                """)
                        .bind("movie_id", movieId)
                        .bind("actor_id", actor.id())
                        .fetch()
                        .rowsUpdated()
                        .then()
                )
                .then();

        return deleteLinks.then(insertLinks);
    }

    private Mono<Movie> enrich(MovieEntity me) {
        return actorRepo.findByMovieId(me.id())
                .map(mapper::toDomain)
                .collectList()
                .map(actors -> mapper.toDomain(me, actors));
    }
}
