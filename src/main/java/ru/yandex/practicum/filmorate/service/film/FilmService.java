package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Film saveFilm(Film film);

    Film updateFilm(Film film);
    Collection<Film> getAll();
    void deleteFilm(Film film);
    void addLike(Integer film, Integer user);

    void deleteLike(Integer film, Integer user);

    Collection<Film> topFilms(Integer count);
}
