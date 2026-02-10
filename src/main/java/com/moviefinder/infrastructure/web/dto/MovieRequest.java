package com.moviefinder.infrastructure.web.dto;

import com.moviefinder.domain.enums.Genre;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;


public record MovieRequest(
        @NotBlank(message = "name is required") String name,
        @NotNull(message = "genre is required") Genre genre,
        @NotNull(message = "publicationDate is required") LocalDate publicationDate,

        @NotNull(message = "actors is required")
        @Size(min = 1, message = "actors must not be empty")
        List<@Valid ActorRequest> actors
) {}
