package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    Film save(Film film);

    Collection<Film> getAll();

    Film update(Film film);

    void deleteFilm(Film film);

    Map<Integer, Film> getFilms();


}
