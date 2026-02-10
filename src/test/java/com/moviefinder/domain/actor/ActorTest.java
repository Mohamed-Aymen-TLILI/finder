package com.moviefinder.domain.actor;

import com.moviefinder.domain.entities.Actor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ActorTest {

    @Test
    void should_fail_when_firstname_blank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Actor(UUID.randomUUID(), "   ", "TLILI"));
    }

    @Test
    void should_fail_when_lastname_blank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Actor(UUID.randomUUID(), "Aymen", ""));
    }
}