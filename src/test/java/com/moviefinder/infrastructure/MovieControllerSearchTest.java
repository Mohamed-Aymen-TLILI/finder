package com.moviefinder.infrastructure;

import com.moviefinder.application.ports.CreateMovieUseCase;
import com.moviefinder.application.ports.GetAllMoviesUseCase;
import com.moviefinder.application.ports.GetMovieByIdUseCase;
import com.moviefinder.application.ports.SearchMoviesByPublicationDateUseCase;
import com.moviefinder.domain.entities.Actor;
import com.moviefinder.domain.entities.Movie;
import com.moviefinder.domain.enums.Genre;
import com.moviefinder.infrastructure.web.controller.MovieController;
import com.moviefinder.infrastructure.web.error.GlobalExceptionHandler;
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
@Import({ MovieDtoMapper.class, GlobalExceptionHandler.class })
class MovieControllerSearchTest {

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
    void search_should_return_200_and_list() {
        LocalDate start = LocalDate.of(2018, 1, 1);
        LocalDate end = LocalDate.of(2020, 12, 31);

        Movie m1 = new Movie(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "The Reactive Spring",
                Genre.ACTION,
                LocalDate.of(2020, 1, 15),
                List.of(new Actor(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), "Aymen", "TLILI"))
        );

        when(search.execute(start, end)).thenReturn(Flux.just(m1));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/movies/search")
                        .queryParam("startDate", "2018-01-01")
                        .queryParam("endDate", "2020-12-31")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo("11111111-1111-1111-1111-111111111111")
                .jsonPath("$[0].publicationDate").isEqualTo("2020-01-15");
    }

    @Test
    void search_should_return_400_when_missing_params() {
        webTestClient.get()
                .uri("/api/movies/search")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void search_should_return_400_when_start_after_end() {
        when(search.execute(
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2020, 1, 1)
        )).thenReturn(Flux.error(new IllegalArgumentException("Start date must be before end date")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/movies/search")
                        .queryParam("startDate", "2021-01-01")
                        .queryParam("endDate", "2020-01-01")
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }
}
