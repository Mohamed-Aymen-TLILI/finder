package com.moviefinder.infrastructure;

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
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieController.class)
@Import(MovieDtoMapper.class)
class MovieControllerGetAllTest {

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
    void getAll_should_return_200_and_list() {
        Movie m1 = new Movie(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "The Reactive Spring",
                Genre.ACTION,
                LocalDate.of(2020, 1, 15),
                List.of(new Actor(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), "Aymen", "TLILI"))
        );

        Movie m2 = new Movie(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "Clean Code SOLID",
                Genre.DRAMA,
                LocalDate.of(2018, 6, 10),
                List.of(new Actor(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"), "Bob", "Eponge"))
        );

        when(getAll.execute()).thenReturn(Flux.just(m1, m2));

        webTestClient.get()
                .uri("/api/movies")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo("11111111-1111-1111-1111-111111111111")
                .jsonPath("$[1].name").isEqualTo("Clean Code SOLID");
    }
}
