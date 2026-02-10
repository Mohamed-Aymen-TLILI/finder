package com.moviefinder.infrastructure.persistence.entities;

import com.moviefinder.domain.enums.Genre;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("movies")
public record MovieEntity(
        @Id UUID id,
        String name,
        Genre genre,
        @Column("publication_date") LocalDate publicationDate
) {}
