package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    void addLike(Long film, Long user);

    void deleteLike(Long film, Long user);

    Collection<Film> topFilms(Integer count);
}
