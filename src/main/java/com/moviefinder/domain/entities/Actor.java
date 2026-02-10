package com.moviefinder.domain.entities;

import java.util.UUID;

public record Actor(UUID id, String firstname, String lastname) {

    public Actor {
        if (firstname == null || firstname.isBlank()) {
            throw new IllegalArgumentException("Actor firstname is required");
        }
        if (lastname == null || lastname.isBlank()) {
            throw new IllegalArgumentException("Actor lastname is required");
        }
    }

    public Actor withId(UUID newId) {
        return new Actor(newId, firstname, lastname);
    }
}
