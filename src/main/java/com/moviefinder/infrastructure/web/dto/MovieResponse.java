package com.moviefinder.infrastructure.web.dto;

import com.moviefinder.domain.enums.Genre;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record MovieResponse(
        UUID id,
        String name,
        Genre genre,
        LocalDate publicationDate,
        List<ActorResponse> actors
) {}
