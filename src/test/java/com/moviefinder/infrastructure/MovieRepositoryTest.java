package com.moviefinder.infrastructure;

import com.moviefinder.domain.ports.MovieRepository;
import com.moviefinder.infrastructure.persistence.adapter.MovieRepositoryAdapter;
import com.moviefinder.infrastructure.persistence.mapper.PersistenceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@Import({
        MovieRepositoryAdapter.class,
        PersistenceMapper.class
})
class MovieRepositoryTest {

    @Autowired
    MovieRepository movieRepository;

    @Test
    void findById_should_return_movie_with_actors() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");

        StepVerifier.create(movieRepository.findById(id))
                .assertNext(m -> {
                    assertThat(m.id()).isEqualTo(id);
                    assertThat(m.name()).isEqualTo("The Reactive Spring");
                    assertThat(m.actors()).hasSize(2);
                    assertThat(m.actors().get(0).firstname()).isNotBlank();
                })
                .verifyComplete();
    }

    @Test
    void findById_should_complete_empty_when_not_found() {
        UUID unknown = UUID.fromString("99999999-9999-9999-9999-999999999999");

        StepVerifier.create(movieRepository.findById(unknown))
                .verifyComplete();
    }

}
