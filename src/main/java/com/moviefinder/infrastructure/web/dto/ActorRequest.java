package com.moviefinder.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ActorRequest(
        @NotBlank(message = "firstname is required") String firstname,
        @NotBlank(message = "lastname is required") String lastname
) {}
