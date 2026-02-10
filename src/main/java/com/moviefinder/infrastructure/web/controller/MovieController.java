package com.moviefinder.infrastructure.web.controller;

import com.moviefinder.application.ports.CreateMovieUseCase;
import com.moviefinder.application.ports.GetAllMoviesUseCase;
import com.moviefinder.application.ports.GetMovieByIdUseCase;
import com.moviefinder.application.ports.SearchMoviesByPublicationDateUseCase;
import com.moviefinder.infrastructure.web.dto.MovieRequest;
import com.moviefinder.infrastructure.web.dto.MovieResponse;
import com.moviefinder.infrastructure.web.mapper.MovieDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final GetAllMoviesUseCase getAll;
    private final GetMovieByIdUseCase getById;
    private final CreateMovieUseCase create;
    private final SearchMoviesByPublicationDateUseCase search;
    private final MovieDtoMapper mapper;

    public MovieController(GetAllMoviesUseCase getAll,
                           GetMovieByIdUseCase getById,
                           CreateMovieUseCase create,
                           SearchMoviesByPublicationDateUseCase search,
                           MovieDtoMapper mapper) {
        this.getAll = getAll;
        this.getById = getById;
        this.create = create;
        this.search = search;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public Mono<MovieResponse> getById(@PathVariable UUID id) {
        return getById.execute(id).map(mapper::toResponse);
    }

    @PostMapping
    public Mono<ResponseEntity<MovieResponse>> create(@Valid @RequestBody MovieRequest request) {
        return Mono.just(request)
                .map(mapper::toDomain)
                .flatMap(create::execute)
                .map(mapper::toResponse)
                .map(resp -> ResponseEntity
                        .created(java.net.URI.create("/api/movies/" + resp.id()))
                        .body(resp));
    }


    @GetMapping
    public Flux<MovieResponse> getAll() {
        return getAll.execute().map(mapper::toResponse);
    }

    @GetMapping("/search")
    public Flux<MovieResponse> search(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return search.execute(startDate, endDate).map(mapper::toResponse);
    }


}
