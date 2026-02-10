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
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieController.class)
@Import(MovieDtoMapper.class)
class MovieControllerCreateTest {

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
    void create_should_return_201_and_created_movie() {
        UUID createdMovieId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID actorId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        when(create.execute(any(Movie.class)))
                .thenReturn(Mono.just(new Movie(
                        createdMovieId,
                        "New Movie",
                        Genre.DRAMA,
                        LocalDate.of(2019, 1, 1),
                        List.of(new Actor(actorId, "Aymen", "TLILI"))
                )));

        String json = """
            {
              "name": "New Movie",
              "genre": "DRAMA",
              "publicationDate": "2019-01-01",
              "actors": [
                { "firstname": "Aymen", "lastname": "TLILI" }
              ]
            }
            """;

        webTestClient.post()
                .uri("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/movies/" + createdMovieId)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(createdMovieId.toString())
                .jsonPath("$.name").isEqualTo("New Movie")
                .jsonPath("$.genre").isEqualTo("DRAMA")
                .jsonPath("$.publicationDate").isEqualTo("2019-01-01")
                .jsonPath("$.actors.length()").isEqualTo(1)
                .jsonPath("$.actors[0].firstname").isEqualTo("Aymen");

    }

    @Test
    void create_should_return_400_when_missing_required_fields() {
        String json = """
            {
              "name": "   ",
              "publicationDate": "2019-01-01",
              "actors": []
            }
            """;

        webTestClient.post()
                .uri("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("BAD_REQUEST");

    }
}
