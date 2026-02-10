package com.moviefinder.infrastructure;

import com.moviefinder.application.errors.MovieNotFoundException;
import com.moviefinder.application.ports.CreateMovieUseCase;
import com.moviefinder.application.ports.GetAllMoviesUseCase;
import com.moviefinder.application.ports.GetMovieByIdUseCase;
import com.moviefinder.application.ports.SearchMoviesByPublicationDateUseCase;
import com.moviefinder.domain.entities.Actor;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.enums.Genre;
import com.moviefinder.infrastructure.web.controller.MovieController;
import com.moviefinder.infrastructure.web.mapper.MovieDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieController.class)
@Import(MovieDtoMapper.class)
class MovieControllerGetByIdTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    GetAllMoviesUseCase getAll;
    @MockitoBean
    GetMovieByIdUseCase getById;
    @MockitoBean
    SearchMoviesByPublicationDateUseCase search;
    @MockitoBean
    CreateMovieUseCase create;

    @Test
    void getById_should_return_200_and_movie_details() {
        UUID movieId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        Movie movie = new Movie(
                movieId,
                "The Reactive Spring",
                Genre.ACTION,
                LocalDate.of(2020, 1, 15),
                List.of(
                        new Actor(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), "Aymen", "TLILI"),
                        new Actor(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), "Taraji", "Dowla")
                )
        );

        when(getById.execute(movieId)).thenReturn(Mono.just(movie));

        webTestClient.get()
                .uri("/api/movies/{id}", movieId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(movieId.toString())
                .jsonPath("$.name").isEqualTo("The Reactive Spring")
                .jsonPath("$.genre").isEqualTo("ACTION")
                .jsonPath("$.publicationDate").isEqualTo("2020-01-15")
                .jsonPath("$.actors.length()").isEqualTo(2)
                .jsonPath("$.actors[0].firstname").isEqualTo("Aymen");
    }

    @Test
    void getById_should_return_404_when_not_found() {
        UUID unknown = UUID.fromString("99999999-9999-9999-9999-999999999999");

        when(getById.execute(unknown)).thenReturn(Mono.error(new MovieNotFoundException(unknown)));

        webTestClient.get()
                .uri("/api/movies/{id}", unknown)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("NOT_FOUND");
    }
}
