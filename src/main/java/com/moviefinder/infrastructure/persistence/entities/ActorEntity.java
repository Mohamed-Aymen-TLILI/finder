package com.moviefinder.infrastructure.persistence.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("actors")
public record ActorEntity(
        @Id UUID id,
        String firstname,
        String lastname
) {}
