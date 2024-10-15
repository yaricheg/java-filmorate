package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;


public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    void delete(Film film);

    Film getFilmById(Integer id);

    Collection<Film> getFilms();

}
