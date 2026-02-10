package com.moviefinder.infrastructure.web.dto;

import java.util.UUID;

public record ActorResponse(
        UUID id,
        String firstname,
        String lastname
) {}
