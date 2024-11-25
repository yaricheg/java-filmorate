CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar(255) NOT NULL,
    login varchar(255) NOT NULL,
    name varchar(255) NOT NULL ,
    birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS mpa (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT NULL,
    description varchar(200),
    release_date date NOT NULL,
    duration INT NOT NULL,
    mpa_id INT NOT NULL,
    rate BIGINT,
    FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id BIGINT,
    friend_id BIGINT,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);


CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT,
    genre_id BIGINT,
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT,
    user_id BIGINT,
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (film_id, user_id)
);

