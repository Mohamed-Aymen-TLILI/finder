INSERT INTO movies (id, name, genre, publication_date) VALUES
                                                           ('11111111-1111-1111-1111-111111111111', 'The Reactive Spring', 'ACTION', '2020-01-15'),
                                                           ('22222222-2222-2222-2222-222222222222', 'Clean Code SOLID', 'DRAMA', '2018-06-10');

INSERT INTO actors (id, firstname, lastname) VALUES
                                                 ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Aymen', 'TLILI'),
                                                 ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Taraji', 'Dowla'),
                                                 ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Bob', 'Eponge');

INSERT INTO movie_actors (movie_id, actor_id) VALUES
                                                  ('11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
                                                  ('11111111-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
                                                  ('22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc');
