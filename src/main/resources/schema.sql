DROP TABLE IF EXISTS movie_actors;
DROP TABLE IF EXISTS actors;
DROP TABLE IF EXISTS movies;

CREATE TABLE movies (
                        id UUID PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        genre VARCHAR(50) NOT NULL,
                        publication_date DATE NOT NULL,
                        CONSTRAINT uk_movies_name_pubdate UNIQUE (name, publication_date)
);

CREATE INDEX idx_movies_publication_date
    ON movies (publication_date);

CREATE TABLE actors (
                        id UUID PRIMARY KEY,
                        firstname VARCHAR(255) NOT NULL,
                        lastname VARCHAR(255) NOT NULL,
                        CONSTRAINT uk_actors_first_last UNIQUE (firstname, lastname)
);

CREATE INDEX idx_actors_lastname
    ON actors (lastname);


CREATE TABLE movie_actors (
                              id BIGSERIAL PRIMARY KEY,

                              movie_id UUID NOT NULL,
                              actor_id UUID NOT NULL,

                              CONSTRAINT fk_movie_actors_movie
                                  FOREIGN KEY (movie_id)
                                      REFERENCES movies (id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_movie_actors_actor
                                  FOREIGN KEY (actor_id)
                                      REFERENCES actors (id)
                                      ON DELETE CASCADE,
                              CONSTRAINT uk_movie_actor UNIQUE (movie_id, actor_id)
);

CREATE INDEX idx_movie_actors_movie_id
    ON movie_actors (movie_id);

CREATE INDEX idx_movie_actors_actor_id
    ON movie_actors (actor_id);

