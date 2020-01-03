INSERT INTO ACTORS (Id, Name, Surname, Birthdate, Deathdate, Gender)
 VALUES (1, 'Joaquin',  'Phoenix',   '1974-10-28', null, 'Male'),
        (2, 'Robert',   'De Niro',   '1943-08-17', null, 'Male'),
        (3, 'Roberto',  'Benigini',  '1952-10-27', null, 'Male'),
        (4, 'Paolo',    'Bonacelli', '1937-02-28', null, 'Male'),
        (5, 'Keira',    'Knightley', '1985-04-26', null, 'Female'),
        (6, 'Jennifer', 'Lawrence',  '1990-07-15', null, 'Female');

INSERT INTO GENRES (Id, Name)
 VALUES (1, 'crime'),
        (2, 'drama'),
        (3, 'thriller'),
        (4, 'comedy'),
        (5, 'western'),
        (6, 'si-fi');

INSERT INTO MOVIES (Id, Title, Duration, ReleaseDate)
 VALUES (1, 'Jocker',         122, '2019-04-19'),
        (2, 'Night on Earth', 129, '1991-12-12');

INSERT INTO MOVIE_ACTORS (ActorId, MovieId, Role)
 VALUES (1, 1, 'Arthur Fleck'),
        (2, 1, 'Murray Franklin'),
        (3, 2, 'Driver (segment "Rome")'),
        (4, 2, 'Priest (segment "Rome")');


INSERT INTO MOVIE_GENRES (GenreId, MovieId, Significance)
 VALUES (1, 1, 1),
        (2, 1, 2),
        (3, 1, 3),
        (4, 2, 1),
        (2, 2, 2);
